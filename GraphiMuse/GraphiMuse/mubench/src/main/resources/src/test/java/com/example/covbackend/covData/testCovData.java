package com.example.covbackend.covData;

import com.example.covbackend.service.impl.CovDataServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Map;

@SpringBootTest
public class testCovData {

    @Autowired
    CovDataServiceImpl covDataServiceImpl;


    @Test
    @DisplayName("缓存建立时间测试")
    @Timeout(10)
    void testCacheTime(){
        covDataServiceImpl.refreshCache();
    }

    @Test
    @DisplayName("国内疫情数据查询测试")
    @Timeout(1)
    void testTestCovData(){


//        //查询国外的疫情数据
//        System.out.println(covDataServiceImpl.getDataByLocationId(0));
//        System.out.println("==================");
//
//        //查询国内的疫情数据
//        System.out.println(covDataServiceImpl.getDataByLocationId(1));
//        System.out.println("==================");
//        covDataServiceImpl.refreshCache();
//        System.out.println("cache create finish!!!!!");
//        for(int i = 2; i <= 20; i++)
//            System.out.println(covDataServiceImpl.getDataByLocationIdWithCache(i));
        Map<String, Object> data = covDataServiceImpl.getDataByLocationNameAndTypeWithCache("海淀区", "2");
        System.out.println(data);
    }
}
