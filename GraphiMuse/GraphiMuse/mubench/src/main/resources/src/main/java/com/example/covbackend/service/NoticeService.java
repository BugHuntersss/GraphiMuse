package com.example.covbackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.covbackend.entity.Notice;

import java.util.List;
import java.util.Map;

/**
 * Notice的Service接口，所有查询返回结果都不会是null
 */
public interface NoticeService extends IService<Notice> {

    List<Map<String, Object>> getAllNotice();
    List<Map<String, Object>> getNoticeByKeywords(String keywords);
}
