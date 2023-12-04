package com.example.covbackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.covbackend.entity.Location;

import java.util.List;
import java.util.Map;
/**
 * Location的Service接口，所有查询返回结果都不会是null
 */
public interface LocationService extends IService<Location> {

    Map<String, Object> getLocationDataByLocationId(int locationId);
    Map<String, Object> getLocationDataByLocationName(String name);
    List<Location> getLocationsByBelongId(int belongId);
    List<Location> getLocationsBylocationIds(List<Integer> list);
    Location getLocationByName(String name, String type);
    Location getLocationById(int locationId);
    String getNameByLocationId(int locationId);
    int getLocationIdByName(String name);
    List<Location> getAllCities();
    void createStationCache();
    void refreshCache();
}
