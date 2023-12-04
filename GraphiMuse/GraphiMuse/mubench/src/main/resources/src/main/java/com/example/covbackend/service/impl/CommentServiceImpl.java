package com.example.covbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.covbackend.entity.Comment;
import com.example.covbackend.mapper.CommentMapper;
import com.example.covbackend.service.CommentService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {

    @Resource
    CommentMapper commentMapper;

    public List<Comment> getCommentByUserId(int user_id){
//        QueryWrapper<Comment> queryWrapper = new QueryWrapper<>();
//        queryWrapper.eq("user_id",user_id);
//
//        return list(queryWrapper);
        return commentMapper.getCommentByUserId(user_id);
    }

    public List<Comment> getCommentByQuestionId(int question_id){
        QueryWrapper<Comment> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("question_id",question_id).orderByAsc("time");
        return list(queryWrapper);
    }

    public void removeCommentsByQuestionId(int question_id){
        QueryWrapper<Comment> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("question_id",question_id);
        remove(queryWrapper);
    }
}
