package com.example.covbackend.location;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.covbackend.controller.LocationController;
import com.example.covbackend.entity.Location;
import com.example.covbackend.service.impl.LocationServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class testLocationService {

    @Autowired
    LocationServiceImpl locationServiceImpl;

    @Autowired
    LocationController locationController;

    @Test
    void testLocationService(){
        Location location = locationServiceImpl.getLocationByName("海淀区", null);
        System.out.println(location);

    }


}
