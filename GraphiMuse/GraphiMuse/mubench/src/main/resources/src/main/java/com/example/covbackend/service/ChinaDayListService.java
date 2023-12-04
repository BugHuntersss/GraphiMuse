package com.example.covbackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.covbackend.entity.ChinaDayList;

import java.util.List;
import java.util.Map;

public interface ChinaDayListService extends IService<ChinaDayList> {
    List<Map<String,Object>> getAllChinaDayList();
}
