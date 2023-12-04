package com.example.covbackend.controller;

import com.example.covbackend.common.Result;
import com.example.covbackend.service.impl.NewsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.Map;


@RestController
public class NewsController {

    @Autowired
    NewsServiceImpl newsServiceImpl;

    @RequestMapping("/allnews")
    Result getAllNews(){

        return Result.succ(newsServiceImpl.getAllNews());
    }
    @RequestMapping("/newsbytime")
    Result getNewsByDate(@RequestBody Map map){

        String date = (String)map.get("date");
        return Result.succ(newsServiceImpl.getNewsByDate(date));

    }
    @RequestMapping("/searchnews")
    Result getNewsByLocationIdAndKeyWords(@RequestBody Map map){
        Object obj1 = map.get("keywords");
        Object obj2 = map.get("locationId");
        if(!(obj1 instanceof String))
            return Result.fail("/searchnews的关键词参数错误");
        String keywords = (String)obj1;
        if(!(obj2 instanceof Integer))
            return Result.succ(newsServiceImpl.getNewsByKeywords(keywords));
        else
            return Result.succ(newsServiceImpl.getNewsByLocationIdAndKeywords((int)obj2, keywords));

    }
    @RequestMapping("/news")
    Result getNewsByLocationId(@RequestBody Map map){
        Object obj = map.get("locationId");

        if(!(obj instanceof Integer))
            return Result.fail("请求参数错误");
        int locationId = (Integer)map.get("locationId");
        return Result.succ(newsServiceImpl.getNewsByLocationId(locationId));
    }
}
