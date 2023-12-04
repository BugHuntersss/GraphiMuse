package com.example.covbackend.notice;

import com.example.covbackend.entity.Notice;
import com.example.covbackend.service.NoticeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Map;

@SpringBootTest
public class testNotice {

    @Autowired
    NoticeService noticeService;

    @Test
    void testNoticeService(){
        List<Map<String, Object>> allNotice = noticeService.getAllNotice();
        for (Map<String, Object> notice : allNotice) {
            System.out.println(notice);
        }
    }
}
