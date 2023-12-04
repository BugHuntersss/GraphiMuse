package com.example.covbackend.controller;

import com.example.covbackend.common.Result;
import com.example.covbackend.service.StationService;
import com.example.covbackend.service.TrainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequestMapping("/train")
@RestController
public class TrainController {
    @Autowired
    TrainService trainService;

    @Autowired
    StationService stationService;


    @RequestMapping("getbykeyword")
    Result getByKeyword(@RequestBody Map map){

        String from = map.get("from").toString();
        String to = map.get("to").toString();
        String keyword = map.get("keyword").toString();
        List<HashMap<String, Object>> trainList = new ArrayList<>();

        try{
            trainList = trainService.getByKeyword(keyword,from,to);
        }catch (Exception e){
            System.out.println(e);
            return Result.fail("获取信息失败");
        }

        return Result.succ(trainList);
    }


}
