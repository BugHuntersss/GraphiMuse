package com.example.covbackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.covbackend.entity.Flight;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface FlightService extends IService<Flight> {
    List<Map<String, Object>> getFlightsByCityAndKeywords(int fromLocationId, int toLocationId, String keywords);
    Map<String, Object> getWarnningDataByCityName(int fromCity, int toCity, String keywords);
    List<Map<String, Object>> getCityList();
    Date getUpdateDate();
}
