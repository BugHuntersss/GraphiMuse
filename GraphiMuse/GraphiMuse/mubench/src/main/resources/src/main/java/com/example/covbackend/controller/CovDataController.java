package com.example.covbackend.controller;


import com.example.covbackend.common.Result;
import com.example.covbackend.service.impl.CovDataServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Map;


@RestController
public class CovDataController {

    @Autowired
    CovDataServiceImpl covDataServiceImpl;

    /**
     * 查询国内疫情数据，没有数据则返回空Hashmap
     * @return
     */
    @RequestMapping("/covdata")
    public Result getCovData(){

        return Result.succ(covDataServiceImpl.getDataByLocationIdWithCache(1));
    }

    /**
     * 查询国际疫情数据（包含中国），没有数据则返回空Hashmap
     * @return
     */
    @RequestMapping("/globalcovdata")
    public Result getDomesticData(){
        Map<String, Object> data = covDataServiceImpl.getDataByLocationIdWithCache(0);
        return Result.succ(data);
    }

    /**
     * 根据名字(必须)和type(可为null)返回疫情数据的查询结果Map，如果没找到则返回一个空HashMap
     * @param map
     * @return
     */
    @RequestMapping("/covdatabyname")
    public Result getCovData(@RequestBody Map map){
        Object name = map.get("name");
        Object type = map.get("type");
        if(!(name instanceof String))
            return Result.fail("请求参数错误");
        if(!(type instanceof String))
            type = null;
        return Result.succ(covDataServiceImpl.getDataByLocationNameAndTypeWithCache((String)name, (String)type));
    }
}
