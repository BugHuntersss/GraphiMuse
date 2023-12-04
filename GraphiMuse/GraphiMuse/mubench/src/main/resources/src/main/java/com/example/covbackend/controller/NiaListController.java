package com.example.covbackend.controller;


import com.example.covbackend.common.Result;
import com.example.covbackend.entity.NiaList;
import com.example.covbackend.error.MyException;
import com.example.covbackend.service.NiaListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RequestMapping("/nialist")
@RestController
public class NiaListController {
    @Autowired
    NiaListService niaListService;

    @RequestMapping("/getportbyprovince")
    Result getPortByProvince(@RequestBody Map map){
        String province = map.get("province").toString();
        List<NiaList> niaList = new ArrayList<>();
        try{
            niaList = niaListService.getByProvince(province);
        }catch (Exception e){
            return Result.fail("无该省份信息");
        }

        HashMap<String, Object> data = new HashMap<>();
        data.put("portlist",niaList);

        return Result.succ(data);
    }

    @RequestMapping("/getportbykeyword")
    Result getPortByKeyword(@RequestBody Map map){
        String keyword = map.get("keyword").toString();
        String province = map.get("province").toString();

        List<NiaList> niaList = new ArrayList<>();
        try{
            niaList = niaListService.getByKeyword(keyword, province);
        }catch (Exception e){
            return Result.fail("无关键字信息");
        }

        HashMap<String, Object> data = new HashMap<>();
        data.put("portlist",niaList);
        //System.out.println(data);
        return Result.succ(data);
    }

    @RequestMapping("/getpolicybykeyword")
    Result getPolicyByKeyword(@RequestBody Map map){
        String keyword = map.get("keyword").toString();

        List<NiaList> niaList = new ArrayList<>();
        try{
            niaList = niaListService.getPolicyByKeyword(keyword);
        }catch (Exception e){
            return Result.fail("无关键字信息");
        }

        HashMap<String, Object> data = new HashMap<>();
        data.put("policylist",niaList);

        return Result.succ(data);
    }
}
