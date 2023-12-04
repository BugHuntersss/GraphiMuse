package com.example.covbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.covbackend.entity.News;
import com.example.covbackend.mapper.NewsMapper;
import com.example.covbackend.service.NewsService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

@Service
public class NewsServiceImpl extends ServiceImpl<NewsMapper, News> implements NewsService {

    /**
     * 获取全部新闻的list，没有找到返回空list
     * @return
     */
    @Override
    public List<News> getAllNews() {

        QueryWrapper<News> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("time");
        List<News> list = list(queryWrapper);

        if(list == null)
            list = new ArrayList<>();

        return list;

    }

    /**
     * 通过日期获取新闻的list,没有找到返回空list
     * dateString的格式应该为"20210709" 或者 "2021-07-09" 或 “2021/07/09”
     * @param dateString
     * @return
     */
    @Override
    public List<News> getNewsByDate(String dateString) {

        //关于日期数据
        String date = dateString.replaceAll("-", "");
        Pattern pattern = Pattern.compile("[0-9]*");
        if(!pattern.matcher(date).matches() || date.length() != 14){
            //throw exception
        }

        QueryWrapper<News> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("time").eq("date", date);
        List<News> list = list(queryWrapper);

        if(list == null)
            list = new ArrayList<>();

        return list;
    }

    /**
     * 通过标题关键词获取新闻的list 没有找到返回空list
     * @param keywords
     * @return
     */
    @Override
    public List<News> getNewsByKeywords(String keywords) {
        QueryWrapper<News> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("time").like("title", "%" + keywords + "%");
        List<News> list = list(queryWrapper);

        if(list == null)
            list = new ArrayList<>();

        return list;
    }

    @Override
    public List<News> getNewsByLocationId(int locationId) {
        QueryWrapper<News> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("location_id", locationId).orderByDesc("time");
        List<News> list = list(queryWrapper);

        if(list == null)
            list = new ArrayList<>();

        return list;
    }

    @Override
    public List<News> getNewsByLocationIdAndKeywords(int locationId, String keywords) {
        QueryWrapper<News> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("location_id", locationId).like("title", "%" + keywords + "%").orderByDesc("time");
        List<News> list = list(queryWrapper);

        if(list == null)
            list = new ArrayList<>();

        return list;
    }

}
