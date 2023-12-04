package com.example.covbackend.controller;

import com.example.covbackend.common.Result;
import com.example.covbackend.service.NoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class NoticeController {
    @Autowired
    NoticeService noticeService;

    @RequestMapping("/allnotices")
    Result allNotice(){
        return Result.succ(noticeService.getAllNotice());
    }

    @RequestMapping("/searchnotices")
    Result searchNotice(@RequestBody Map map){
        Object obj = map.get("keywords");
        if(!(obj instanceof String))
            return Result.succ(noticeService.getAllNotice());
        else
            return Result.succ(noticeService.getNoticeByKeywords((String)obj));

    }
}
