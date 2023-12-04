package com.example.covbackend.controller;


import com.example.covbackend.common.Result;
import com.example.covbackend.entity.Location;
import com.example.covbackend.service.*;
import com.example.covbackend.util.DateUtil;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RequestMapping("/history")
@RestController
public class HistoryController {
    @Resource
    ChinaDayListService chinaDayListService;

    @Resource
    ChinaVaccinationDayListService chinaVaccinationDayListService;

    @Resource
    OverseasDayListService overseasDayListService;

    @Resource
    CovDayListService covDayListService;

    @RequestMapping("/getallchinadaylist")
    Result getAllChinaDayList() {
        List<Map<String, Object>> chinaDayList = chinaDayListService.getAllChinaDayList();
        List<Map<String, Object>> vaccinationDayList = chinaVaccinationDayListService.getAllChinaVaccinationDayList();
        for (int i = 0; i < chinaDayList.size() && i < vaccinationDayList.size(); i++) {
            chinaDayList.get(i).putAll(vaccinationDayList.get(i));
        }
        return Result.succ(chinaDayList);
    }

    @RequestMapping("/overseasdaylist")
    Result getLatestOverseasDayList() { return Result.succ(overseasDayListService.getLatestOverseasDayList());}

    @RequestMapping("/getallprovincedaylist")
    Result getAllProvinceDayList() {
        return Result.succ(covDayListService.getCovDayListByLocationIdWithCache(1));
    }

    @RequestMapping("/getallcountrydaylist")
    Result getAllCountryDayList() {
        return Result.succ(covDayListService.getCovDayListByLocationIdWithCache(0));
    }

}
