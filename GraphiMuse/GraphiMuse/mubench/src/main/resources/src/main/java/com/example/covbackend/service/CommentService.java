package com.example.covbackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.covbackend.entity.Comment;

import java.util.List;

public interface CommentService extends IService<Comment> {
    List<Comment> getCommentByUserId(int user_id);
    List<Comment> getCommentByQuestionId(int question_id);
    void removeCommentsByQuestionId(int question_id);
}
