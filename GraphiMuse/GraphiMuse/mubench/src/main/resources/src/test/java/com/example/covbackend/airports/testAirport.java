package com.example.covbackend.airports;

import com.example.covbackend.service.impl.AirportServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class testAirport {

    @Autowired
    AirportServiceImpl airportServiceImpl;

    @Test
    void testAirPortService(){

    }
}
