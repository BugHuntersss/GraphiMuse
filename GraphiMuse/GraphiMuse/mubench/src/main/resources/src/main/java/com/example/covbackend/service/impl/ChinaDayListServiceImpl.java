package com.example.covbackend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.covbackend.entity.ChinaDayList;
import com.example.covbackend.mapper.ChinaDayListMapper;
import com.example.covbackend.service.ChinaDayListService;
import com.example.covbackend.util.DateUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ChinaDayListServiceImpl extends ServiceImpl<ChinaDayListMapper, ChinaDayList> implements ChinaDayListService {
    @Resource
    DateUtil dateUtil;

    @Override
    public List<Map<String, Object>> getAllChinaDayList() {
        List<Map<String,Object>> maps = listMaps();
        if(maps == null)
            maps = new ArrayList<>();
        dateUtil.addDatetime(maps);
        return maps;
    }
}
