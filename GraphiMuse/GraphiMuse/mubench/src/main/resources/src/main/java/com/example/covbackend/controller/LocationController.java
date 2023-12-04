package com.example.covbackend.controller;

import com.example.covbackend.common.Result;
import com.example.covbackend.entity.Location;
import com.example.covbackend.entity.Station;
import com.example.covbackend.service.StationService;
import com.example.covbackend.service.impl.LocationServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class LocationController {

    @Autowired
    LocationServiceImpl locationServiceImpl;
    @Autowired
    StationService stationService;

    /**
     * reponsebody接受一个belongid或者name+type(可选)作为参数进行查询
     * 查询belongid时返回一个location list，没有找到则返回一个空list
     * 查询name+type时返回一个location，没有找到则报错
     * @param map
     * @return
     */
    @RequestMapping("/locations")
    Result getLocations(@RequestBody Map map){

        Object belongId = map.get("belongId");
        if(!(belongId instanceof Integer)){
            //根据名字和类别查询
            Object name = map.get("name");
            Object type = map.get("type");
            if(!(type instanceof String) || type.equals(""))
                type = null;
            if(!(name instanceof String))
                return Result.fail("请求参数错误");
            else{
                Location location = locationServiceImpl.getLocationByName((String) name, (String) type);
                if(location == null)
                    return Result.fail("没有找到结果");
                else
                    return Result.succ(location);
            }
        }
        //根据belongid查询
        return Result.succ(locationServiceImpl.getLocationsByBelongId((int) belongId));
    }

    @RequestMapping("/getcity")
    Result getAllCityAndStation(@RequestBody Map map){
        HashMap<String, Object> data = new HashMap<>();
        data.put("citylist",locationServiceImpl.getCitiesAndStationsByCache());
        return Result.succ(data);
    }
}
