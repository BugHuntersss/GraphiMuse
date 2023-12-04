package com.example.covbackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.covbackend.entity.News;

import java.util.Date;
import java.util.List;

public interface NewsService extends IService<News> {

    //获取全部新闻的list，没有找到返回空list
    List<News> getAllNews();
    //通过日期获取新闻的list dateString的格式应该为"20210709" 或者 "2021-07-09" 或 “2021/07/09”,没有找到返回空list
    List<News> getNewsByDate(String dateString);
    //通过标题关键词获取新闻的list 没有找到返回空list
    List<News> getNewsByKeywords(String keywords);
    //通过location_id搜索新闻，没有找到返回空list
    List<News> getNewsByLocationId(int locationId);
    //通过location_id和标题关键词搜索新闻，没有找到返回空list
    List<News> getNewsByLocationIdAndKeywords(int locationId, String keywords);
}
