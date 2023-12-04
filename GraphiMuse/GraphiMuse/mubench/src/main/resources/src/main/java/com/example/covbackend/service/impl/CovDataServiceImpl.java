package com.example.covbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.covbackend.entity.CovData;
import com.example.covbackend.entity.Location;
import com.example.covbackend.entity.User;
import com.example.covbackend.mapper.CovDataMapper;
import com.example.covbackend.mapper.UserMapper;
import com.example.covbackend.service.CovDataService;
import com.example.covbackend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.ManagedMap;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CovDataServiceImpl extends ServiceImpl<CovDataMapper, CovData> implements CovDataService{

    @Autowired
    LocationServiceImpl locationService;

    @Autowired
    CovDataMapper covDataMapper;

    Map<Integer, Map<String, Object>> cache = new HashMap<>();
    boolean cacheIsNew = true;

    @Override
    public void clearCache(){
        cacheIsNew = true;
        cache = null;
    }


    @Override
    public void refreshCache(){
        if(cache==null) cache = new HashMap<>();
        this.createCache(0);
        this.createCache(1);
        Map<String, Object> location = getDataByLocationId(1);
        //获取中国数据
        ((ArrayList)this.getCache(0).get("locations")).add(location);
        this.cacheIsNew = false;
    }

    /**
     * 根据location_id获取疫情数据Map（不包含子对象），没有找到返回一个空Hashmap
     * @param locationId
     * @return
     */
    @Override
    public Map<String, Object> getDataByLocationId(int locationId) {
        //操作locations表 获取该地方location对象的map
        Map<String, Object> map = locationService.getLocationDataByLocationId(locationId);
        QueryWrapper<CovData> queryWrapper = new QueryWrapper<>();

        //操作covdata表 获取该地方的疫情数据
        queryWrapper.eq("location_id", map.get("id"));
        Map<String, Object> covData = getMap(queryWrapper);
        if(covData != null) {
            covData.remove("id");
            covData.remove("location_id");
        } else {
            covData = new HashMap<>();
        }
        map.putAll(covData);
        map.put("cov_data", covData);
        map.put("value", covData.get("now_confirm"));
        return map;
    }

    /**
     * 根据location_id获取疫情数据Map（包含子地区），没有找到返回一个空Hashmap
     * 数据表belong_id必须正确，否则陷入死循环
     * Map对象
     * {
     *      id
     *      name,
     *      type,
     *      risk,
     *      belong_id,
     *      cov_data[],
     *      locations[] //属于该地名的子地名对象
     * }
     * @param locationId
     * @return
     */
    @Override
    public Map<String, Object> getDataByLocationIdWithCache(int locationId){
        if(!cacheIsNew){
            Map<String, Object> cache = getCache(locationId);
            if(cache == null)
                return new HashMap<>();
            return cache;
        }
        else{
            refreshCache();
            Map<String, Object> cache = getCache(locationId);
            if(cache == null)
                return new HashMap<>();
            return cache;
        }

    }

    /**
     * 根据地名(不可为null)和类型(可为null)获取疫情数据Map（包含子地区）。如果找不到该地方则返回一个空Hashmap。
     * @param name
     * @param type
     * @return
     */
    @Override
    public Map<String, Object> getDataByLocationNameAndTypeWithCache(String name, String type) {
        //操作location表 获取一个location
        Location location = locationService.getLocationByName(name, type);
        if(location == null)
            return new HashMap<>();
        else
            return getDataByLocationIdWithCache(location.getId());
    }

    /**
     * 递归创建缓存
     * @param locationId
     * @return
     */
    private Map<String, Object> createCache(int locationId){
        //获取该地方的疫情数据
        Map<String, Object> map = getDataByLocationId(locationId);

        //操作locations表 获取该地方的子地方location对象集合
        List<Location> childLocations = locationService.getLocationsByBelongId((int) map.get("id"));

        //操作covdata表 递归获取所有子地方的疫情数据
        //死循环风险!!
        ArrayList<Map<String, Object>> childs = new ArrayList<>();

        if(childLocations != null)
            for (Location childLocation : childLocations)
                childs.add(createCache(childLocation.getId()));

        map.put("locations", childs);
        cache.put(locationId, map);
        return map;
    }

    /**
     * 根据locationid获取缓存（包含子地方的疫情数据）
     * @param locationId
     * @return
     */
    private Map<String, Object> getCache(int locationId){
        return cache.get(locationId);
    }



}
