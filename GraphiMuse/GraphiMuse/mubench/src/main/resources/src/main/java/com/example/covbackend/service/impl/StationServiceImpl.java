package com.example.covbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.covbackend.entity.Question;
import com.example.covbackend.entity.Station;
import com.example.covbackend.mapper.StationMapper;
import com.example.covbackend.service.StationService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StationServiceImpl extends ServiceImpl<StationMapper, Station> implements StationService {
    public Station getStationByName(String name){
        QueryWrapper<Station> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("name",name);
        Station station = new Station();
        try{
            station  = getOne(queryWrapper);
        }catch (Exception e){
            return station;
        }
        return station;
    }

    public List<Station> getStationByLocationId(int location_id){
        QueryWrapper<Station> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("location_id",location_id);
        return list(queryWrapper);
    }
}
