package com.example.covbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.covbackend.entity.Question;
import com.example.covbackend.entity.User;
import com.example.covbackend.mapper.QuestionMapper;
import com.example.covbackend.mapper.UserMapper;
import com.example.covbackend.service.QuestionService;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class QuestionServiceImpl extends ServiceImpl<QuestionMapper, Question> implements QuestionService {

    @Resource
    QuestionMapper questionMapper;
    @Resource
    UserMapper userMapper;
    public List<Question> getQuestionByUserId(int userid){
        return questionMapper.getQuestionByUserId(userid);
    };

    public List<HashMap<String,Object>> getQuestionByKeyword(String keyword){
        QueryWrapper<Question> queryWrapper = new QueryWrapper<>();
        queryWrapper.like("title","%"+keyword+"%");
        List<Question> questionList = list(queryWrapper);

        List<HashMap<String, Object>> questionlist = new ArrayList<>();

        for(int i = 0; i<questionList.size(); i++){
            HashMap<String, Object> question = new HashMap<>();
            question.put("title",questionList.get(i).getTitle());
            question.put("time",questionList.get(i).getTime());
            question.put("id",questionList.get(i).getId());
            question.put("content",questionList.get(i).getContent());
            User u  = userMapper.getUserById(questionList.get(i).getUserId());
            if(u==null) continue;
            question.put("user_id",u.getId());
            question.put("username",u.getUsername());
            questionlist.add(question);
        }
        return questionlist;
    }

    public List<HashMap<String, Object>> getNewestQuestion(){
        QueryWrapper<Question> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("time");
        List<Question> questionList = list(queryWrapper);
        List<HashMap<String, Object>> questionlist = new ArrayList<>();
        for(int i = 0; i<questionList.size(); i++){
            HashMap<String, Object> question = new HashMap<>();
            question.put("title",questionList.get(i).getTitle());
            question.put("time",questionList.get(i).getTime());
            question.put("content",questionList.get(i).getContent());
            question.put("id",questionList.get(i).getId());
            User u  = userMapper.getUserById(questionList.get(i).getUserId());
            if(u==null) continue;
            question.put("user_id",u.getId());
            question.put("username",u.getUsername());
            questionlist.add(question);
        }
        return questionlist;
    }



}
