package com.example.covbackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.covbackend.entity.OverseasDayList;

import java.util.List;
import java.util.Map;

public interface OverseasDayListService extends IService<OverseasDayList> {
    List<Map<String,Object>> getLatestOverseasDayList();
}
