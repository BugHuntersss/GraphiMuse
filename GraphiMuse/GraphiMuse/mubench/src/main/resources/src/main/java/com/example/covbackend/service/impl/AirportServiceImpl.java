package com.example.covbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.covbackend.entity.Airport;
import com.example.covbackend.mapper.AirportMapper;
import com.example.covbackend.service.AirportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AirportServiceImpl extends ServiceImpl<AirportMapper, Airport> implements AirportService {

    @Autowired
    LocationServiceImpl locationServiceImpl;

    @Override
    public List<String> getAirPortsNameByLocationId(int locationId) {
        QueryWrapper<Airport> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("location_id", locationId);
        List list = list(queryWrapper);
        ArrayList<String> airportsName = new ArrayList<>();
        for (Object o : list) {
            airportsName.add(((Airport)o).getName());
        }
        return airportsName;
    }
}
