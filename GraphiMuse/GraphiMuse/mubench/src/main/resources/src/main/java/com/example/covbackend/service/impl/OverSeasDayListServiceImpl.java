package com.example.covbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.covbackend.entity.OverseasDayList;
import com.example.covbackend.mapper.OverseasDayListMapper;
import com.example.covbackend.service.OverseasDayListService;
import com.example.covbackend.util.DateUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

@Service
public class OverSeasDayListServiceImpl extends ServiceImpl<OverseasDayListMapper, OverseasDayList> implements OverseasDayListService{
    @Resource
    DateUtil dateUtil;

    @Override
    public List<Map<String, Object>> getLatestOverseasDayList() {
        QueryWrapper<OverseasDayList> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("id");
        List<Map<String,Object>> maps = listMaps(queryWrapper);
        if(maps == null)
            maps = new ArrayList<>();
        Collections.reverse(maps);
        dateUtil.addDatetime(maps);
        return maps;
    }
}
