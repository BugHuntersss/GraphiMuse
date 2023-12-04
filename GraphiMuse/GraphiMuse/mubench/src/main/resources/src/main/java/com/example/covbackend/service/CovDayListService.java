package com.example.covbackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.covbackend.entity.CovDayList;

import java.util.List;
import java.util.Map;

public interface CovDayListService extends IService<CovDayList> {
    List<List<Map<String, Object>>> getCovDayListByLocationIdWithCache(int locationId);
    void clearCache();
    void refreshCache();
}
