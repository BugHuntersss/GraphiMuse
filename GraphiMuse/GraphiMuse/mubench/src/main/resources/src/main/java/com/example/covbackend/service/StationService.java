package com.example.covbackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.covbackend.entity.Station;

import java.util.List;

public interface StationService extends IService<Station> {
    Station getStationByName(String name);
    List<Station> getStationByLocationId(int location_id);
}
