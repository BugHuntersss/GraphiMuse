package com.example.covbackend.flight;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.covbackend.entity.Flight;
import com.example.covbackend.service.impl.AirportServiceImpl;
import com.example.covbackend.service.impl.FlightServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Map;

@SpringBootTest
public class testFlight {

    @Autowired
    FlightServiceImpl flightServiceImpl;

    @Autowired
    AirportServiceImpl airportServiceImpl;

    @Test
    void testFlightService(){
        List<String> fromAirports = airportServiceImpl.getAirPortsNameByLocationId(115);
        List<String> toAirports = airportServiceImpl.getAirPortsNameByLocationId(7);
        QueryWrapper<Flight> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("from_airport", fromAirports).in("to_airport", toAirports);
        List<Map<String, Object>> maps = flightServiceImpl.listMaps(queryWrapper);
        System.out.println(maps);
    }

}
