package com.example.covbackend;

import com.example.covbackend.service.CovDataService;
import com.example.covbackend.service.CovDayListService;
import com.example.covbackend.service.LocationService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class CovbackendApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext run = SpringApplication.run(CovbackendApplication.class, args);

        CovDataService covDataServiceImpl = (CovDataService)run.getBean("covDataServiceImpl");
        covDataServiceImpl.refreshCache();
        System.out.println("Cache loads success");
        CovDayListService covDayListServiceImpl = (CovDayListService) run.getBean("covDayListServiceImpl");
        covDayListServiceImpl.refreshCache();
        System.out.println("Chart Cache loads success");
        LocationService locationServiceImpl = (LocationService) run.getBean("locationServiceImpl");
        locationServiceImpl.refreshCache();
        System.out.println("Station Cache loads success");

    }
}
