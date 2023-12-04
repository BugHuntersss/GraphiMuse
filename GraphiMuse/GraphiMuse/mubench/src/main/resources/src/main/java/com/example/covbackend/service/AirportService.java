package com.example.covbackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.covbackend.entity.Airport;

import java.util.List;

public interface AirportService extends IService<Airport> {

    List<String> getAirPortsNameByLocationId(int locationId);

}
