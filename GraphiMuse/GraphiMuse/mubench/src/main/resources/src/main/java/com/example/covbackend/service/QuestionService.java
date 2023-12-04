package com.example.covbackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.covbackend.entity.Location;
import com.example.covbackend.entity.Question;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface QuestionService  extends IService<Question> {

    List<Question> getQuestionByUserId(int userid);
    List<HashMap<String,Object>> getQuestionByKeyword(String keyword);
    List<HashMap<String,Object>> getNewestQuestion();

}
