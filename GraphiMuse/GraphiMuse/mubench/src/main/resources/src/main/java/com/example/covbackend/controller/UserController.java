package com.example.covbackend.controller;



import com.example.covbackend.annotations.UserLoginToken;
import com.example.covbackend.common.Result;
import com.example.covbackend.entity.CovData;
import com.example.covbackend.entity.Location;
import com.example.covbackend.entity.Question;
import com.example.covbackend.entity.User;
import com.example.covbackend.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.example.covbackend.util.TokenUtil;

import com.example.covbackend.service.impl.TokenService;

@RequestMapping("/user")
@RestController
public class UserController {

    @Resource
    UserService userService;

    @Autowired
    TokenService tokenService;

    @Resource
    SubscriptionService subscriptionService;

    @Resource
    LocationService locationService;

    @Resource
    QuestionService questionService;

    @Resource
    CovDataService covDataService;

    @RequestMapping("/login")
    public Result login(@RequestBody Map map,
                        HttpServletResponse response,
                        HttpServletRequest request){
        String mail = map.get("mail").toString();
        String password = map.get("password").toString();

        if(mail == null || password == null)
            return Result.fail("无用户名或密码参数");

        User user = userService.getUserByMail(mail);

        if(user == null){
            return Result.fail("用户名不存在");
        } else if(!password.equals(user.getPassword())){
            return Result.fail("密码错误");
        }
        HashMap<String, User> data = new HashMap<>();
        data.put("user", user);

        String token = tokenService.getToken(user);
        Cookie cookie = new Cookie("token", token);
        cookie.setPath("/");
        response.addCookie(cookie);
        System.out.println(token);

        return Result.succ("登录成功", data);
    }

    @RequestMapping(value = "/register")
    public Result register(@RequestBody Map map) {
        User user = new User();

        user.setUsername(map.get("username").toString());
        user.setPassword(map.get("password").toString());
        user.setMail(map.get("mail").toString());
        System.out.println(map.get("mail").toString());
        System.out.println(map.get("username").toString());

        try{
            userService.insert(user);
        }catch(Exception e)
        {
            System.out.println(e);
            return Result.fail("插入失败");
        }

        HashMap<String, User> data = new HashMap<>();
        data.put("user", user);

        return Result.succ("注册成功",data);
    }


    @UserLoginToken
    @RequestMapping(value = "/checkoldpassword")
    public Result checkOldPassword(@RequestBody Map map){
        int id = Integer.parseInt(TokenUtil.getTokenUserId());
        User user = userService.getUserById(id);

        String old_password = map.get("old_password").toString();

        if(!old_password.equals(user.getPassword())) return Result.fail("密码错误");

        return Result.succ();
    }

    @UserLoginToken
    @RequestMapping(value = "/changepassword")
    public Result changePassword(@RequestBody Map map){
        int id = Integer.parseInt(TokenUtil.getTokenUserId());
        User user = userService.getUserById(id);

        String old_password = map.get("old_password").toString();
        if(!old_password.equals(user.getPassword())) return Result.fail("原密码错误");

        String new_password = map.get("new_password").toString();
        user.setPassword(new_password);
        userService.updateById(user);

        return Result.succ();
    }

    @RequestMapping(value = "/checkmail")
    public Result checkMail(@RequestBody Map map){

        String mail = map.get("mail").toString();
        System.out.println(userService.duplicatedMail(mail));
        if(userService.duplicatedMail(mail)) return Result.fail("邮箱已被注册");
        return Result.succ();
    }

    @UserLoginToken
    @RequestMapping(value = "/showpersoninfo")
    public Result showPersonInfo(@RequestBody Map map){
        int id = Integer.parseInt(TokenUtil.getTokenUserId());
        User user = userService.getUserById(id);

        if(user==null) return Result.fail("查找用户失败");

        int[] LocationIdList = subscriptionService.getLocationIdByUserId(id);

        List<Map<String,Object>> subscribeDataList = new ArrayList<>();
        for(int i=0; i<LocationIdList.length; i++){
            subscribeDataList.add(covDataService.getDataByLocationIdWithCache(LocationIdList[i]));
        }

        Location loc = locationService.getById(user.getLocation_id());
        Map<String,Object> my_covdata = covDataService.getDataByLocationIdWithCache(loc.getId());

        HashMap<String,Object> data = new HashMap<>();
        data.put("user",user);
        data.put("subscribedatalist",subscribeDataList);
        data.put("my_province",my_covdata);
        return Result.succ(data);
    }

    @UserLoginToken
    @RequestMapping(value = "/subscribe")
    public Result subscribe(@RequestBody Map map){
        int id = Integer.parseInt(TokenUtil.getTokenUserId());
        int loc_id = Integer.parseInt(map.get("loc_id").toString());

        try{
            subscriptionService.insert(id,loc_id);
        }catch (Exception e){
            return Result.fail("订阅失败，请重新订阅");
        }

        return Result.succ();
    }

    @UserLoginToken
    @RequestMapping(value = "/getusername")
    public Result getUsername(@RequestBody Map map){
        int id = Integer.parseInt(TokenUtil.getTokenUserId());

        User u = new User();
        try{
            u=userService.getUserById(id);
        }catch (Exception e){
            return Result.fail("");
        }
        HashMap<String, Object> data = new HashMap<>();
        data.put("username", u.getUsername());
        return Result.succ(data);
    }

    @UserLoginToken
    @RequestMapping(value = "/changeinfo")
    public Result changeInfo(@RequestBody Map map){
        int id = Integer.parseInt(TokenUtil.getTokenUserId());

        //System.out.println(map);

        int location_id = Integer.parseInt( map.get("location_id").toString() );
        String introduction = map.get("introduction").toString();
        String username = map.get("username").toString();

        User u = new User();
        try{
            u = userService.getUserById(id);
        }catch (Exception e){
            return Result.fail("登录失效，请重新登录");
        }

        if(username != null) u.setUsername(username);
        u.setLocation_id(location_id);
        u.setIntroduction(introduction);

        try{
            userService.updateById(u);
        }catch(Exception e){
            return Result.fail("用户名或邮箱已被注册，请修改");
        }

        HashMap<String,Object> data = new HashMap<>();
        data.put("user",u);

        return Result.succ(data);

    }

    @UserLoginToken
    @RequestMapping(value = "/logout")
    public Result logout(@RequestBody Map map,
                         HttpServletResponse response) {
        Cookie cookie = new Cookie("token", "过期");
        //将cookie设置在根目录下面
        cookie.setPath("/");
        //设置cookie的有效期，单位是秒(一年)
        //如果这个maxage不设置的话，cookie就不会写入硬盘，而是写在内存。只在当前页面有效。
        cookie.setMaxAge(0);
        System.out.println("cookie完成设置");
        response.addCookie(cookie);
        System.out.println(response);

        return Result.succ();
    }
}
