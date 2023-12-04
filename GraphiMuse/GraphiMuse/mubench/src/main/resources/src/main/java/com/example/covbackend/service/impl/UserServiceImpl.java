package com.example.covbackend.service.impl;
import com.example.covbackend.common.Result;
import com.example.covbackend.entity.User;
import com.example.covbackend.mapper.UserMapper;
import com.example.covbackend.service.UserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Resource
    UserMapper userMapper;

    public User getUserById(int id){
        User user = userMapper.getUserById(id);

        System.out.println(user);
        return user;
    }

    public User getUserByUsername(String username){
        User user = userMapper.getUserByUsername(username);
        return user;
    }

    public void insert(User user){ userMapper.insert(user); }

    public User getUserByMail(String mail){
        return userMapper.getUserByMail(mail);
    }

    public Boolean duplicatedMail(String mail){
        User u = userMapper.getUserByMail(mail);

        if(u==null){
            return false;
        }

        else{
            return true;
        }
    };
}