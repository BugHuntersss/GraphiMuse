package com.example.covbackend.news;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.covbackend.controller.NewsController;
import com.example.covbackend.entity.News;
import com.example.covbackend.service.impl.NewsServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class testNews {

    @Autowired
    NewsServiceImpl newsServiceImpl;

    @Test
    @DisplayName("通过时间获取新闻")
    void testGetNewsByTime(){
        List<News> newsByDate = newsServiceImpl.getNewsByDate("20210709");
        for (News news : newsByDate) {
            System.out.println(news);
        }
    }

    @Test
    @DisplayName("获取全部新闻")
    void testGetAllNews(){
        List<News> allNews = newsServiceImpl.getAllNews();
        for (News news : allNews) {
            System.out.println(news);
        }
    }

    @Test
    @DisplayName("通过关键字获取新闻")
    void testGetNewsByKeywords(){
        List<News> allNews = newsServiceImpl.getNewsByKeywords("病例");
        for (News news : allNews) {
            System.out.println(news);
        }
    }

    @Test
    @DisplayName("通过locationId获取新闻")
    void testGetNewsByLocationsId(){
        List<News> allNews = newsServiceImpl.getNewsByLocationId(1);
        for (News news : allNews) {
            System.out.println(news);
        }
    }




}
