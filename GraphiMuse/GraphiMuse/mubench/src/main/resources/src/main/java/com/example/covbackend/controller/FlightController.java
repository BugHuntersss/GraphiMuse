package com.example.covbackend.controller;

import com.example.covbackend.common.Result;
import com.example.covbackend.service.impl.FlightServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.management.MalformedObjectNameException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class FlightController {

    @Autowired
    FlightServiceImpl flightServiceImpl;

    @RequestMapping("/flights")
    Result getFlightsByFromAndTo(@RequestBody Map map){
        Object from = map.get("from");
        Object to = map.get("to");
        if(!(from instanceof Integer) || !(to instanceof Integer))
            Result.fail("请求参数错误");

        return Result.succ(flightServiceImpl.getWarnningDataByCityName(Integer.parseInt(from.toString()),
                Integer.parseInt(to.toString()),
                ""));


    }
    @RequestMapping("/flightsinfo")
    Result getFlightsInfoByLocationId(@RequestBody Map map){
        Object from = map.get("from");
        Object to = map.get("to");
        Object keywords = map.get("keywords");
        if(!(from instanceof Integer) || !(to instanceof Integer))
            Result.fail("请求参数错误");

        if(keywords == null)
            keywords = "";

        return Result.succ(flightServiceImpl.getWarnningDataByCityName(Integer.parseInt(from.toString()),
                Integer.parseInt(to.toString()),
                keywords.toString()
                ));

    }
    @RequestMapping("/citylist")
    Result getCityList(){
        Map<String, Object> map = new HashMap<>();
        List<Map<String, Object>> cityList = flightServiceImpl.getCityList();
        Date updateDate = flightServiceImpl.getUpdateDate();
        map.put("citylist", cityList);
        map.put("updatedate", updateDate);
        return Result.succ(map);
    }
}
