package com.example.covbackend.config;

import com.example.covbackend.service.CovDataService;
import com.example.covbackend.service.CovDayListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDateTime;

@Configuration      //1.主要用于标记配置类，兼备Component的效果。
@EnableScheduling   // 2.开启定时任务
public class RefreshCache {
    @Autowired
    CovDataService covDataService;

    @Autowired
    CovDayListService covDayListService;

    @Scheduled(cron = "0 0 0/1 * * ?")
    //或直接指定时间间隔，例如：5秒
    //@Scheduled(fixedRate=5000)
    private void configureTasks() {
        System.out.println("执行中");
        covDataService.clearCache();
        covDataService.refreshCache();
        System.out.println("Cache refresh success");
        covDayListService.clearCache();
        covDayListService.refreshCache();
        System.out.println("Chart Cache refresh success");
        System.err.println("执行静态定时任务时间: " + LocalDateTime.now());
    }
}
