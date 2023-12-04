package com.example.covbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.covbackend.entity.CovData;
import com.example.covbackend.entity.Notice;
import com.example.covbackend.mapper.NoticeMapper;
import com.example.covbackend.service.NoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 */
@Service
public class NoticeServiceImpl extends ServiceImpl<NoticeMapper, Notice> implements NoticeService {

    @Autowired
    NoticeMapper noticeMapper;

    @Override
    public List<Map<String, Object>> getAllNotice() {
        QueryWrapper<Notice> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("time");
        List<Map<String, Object>> maps = listMaps(queryWrapper);
        if(maps == null)
            maps = new ArrayList<>();
        return maps;
    }

    @Override
    public List<Map<String, Object>> getNoticeByKeywords(String keywords) {
        QueryWrapper<Notice> queryWrapper = new QueryWrapper<>();
        queryWrapper.like("title", "%" + keywords + "%").orderByDesc("time");
        List<Map<String, Object>> maps = listMaps(queryWrapper);
        if(maps == null)
            maps = new ArrayList<>();
        return maps;
    }

}
