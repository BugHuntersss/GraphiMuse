package com.example.covbackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.covbackend.entity.ChinaVaccinationDayList;

import java.util.List;
import java.util.Map;

public interface ChinaVaccinationDayListService extends IService<ChinaVaccinationDayList> {
    List<Map<String,Object>> getAllChinaVaccinationDayList();
}
