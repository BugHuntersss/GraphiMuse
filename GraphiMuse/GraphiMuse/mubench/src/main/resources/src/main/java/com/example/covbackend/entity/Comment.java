package com.example.covbackend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;


/**
 * 评论表
 */
@Data
@TableName("comments")
public class Comment {

    @TableId(value = "id", type = IdType.AUTO)
    int id;

    int userId;
    int questionId;
    String content;
    Date time;
}
