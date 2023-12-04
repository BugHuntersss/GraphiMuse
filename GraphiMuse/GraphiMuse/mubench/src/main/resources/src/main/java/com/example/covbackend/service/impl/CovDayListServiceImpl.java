package com.example.covbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.covbackend.entity.CovDayList;
import com.example.covbackend.entity.Location;
import com.example.covbackend.mapper.CovDataMapper;
import com.example.covbackend.mapper.CovDayListMapper;
import com.example.covbackend.service.CovDayListService;
import com.example.covbackend.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.sql.Date;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class CovDayListServiceImpl extends ServiceImpl<CovDayListMapper, CovDayList> implements CovDayListService {
    @Autowired
    LocationServiceImpl locationService;

    @Autowired
    CovDataMapper covDataMapper;

    @Resource
    DateUtil dateUtil;

    Map<Integer, List<List<Map<String, Object>>>> cache = new HashMap<>();
    boolean cacheIsNew = true;
    //初始化日期列表

    @Override
    public void clearCache() {
        cacheIsNew = true;
        cache = null;
    }

    @Override
    public void refreshCache() {
        if(cache==null) cache = new HashMap<>();
        this.createCache(0);
        this.createCache(1);
        this.cacheIsNew = false;
    }

    @Override
    public List<List<Map<String, Object>>> getCovDayListByLocationIdWithCache(int locationId) {
        if(!cacheIsNew)
            return getCache(locationId);
        else
            return createCache(locationId);
    }

    private List<List<Map<String, Object>>> getCache(int locationId){
        return cache.get(locationId);
    }

    private List<List<Map<String, Object>>> createCache(int locationId){
        List<Location> childLocations = locationService.getLocationsByBelongId(locationId);
        List<List<Map<String,Object>>> childs = new ArrayList<>();
        if(childLocations != null)
            for (Location childLocation : childLocations)
                childs.add(getCovDayListByLocationId(childLocation.getId()));
        //操作locations表 获取该地方location对象的map
        cache.put(locationId, childs);
        return childs;
    }

    public List<Map<String, Object>> getCovDayListByLocationId(int locationId) {
        QueryWrapper<CovDayList> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("id");
        queryWrapper.eq("location_id", locationId);
        List<Map<String,Object>> maps = listMaps(queryWrapper);
        if(maps == null)
            maps = new ArrayList<>();
        Collections.reverse(maps);
        dateUtil.addDatetime(maps);
        return maps;
    }
}
