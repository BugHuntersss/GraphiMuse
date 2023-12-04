package com.example.covbackend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 问题表
 */
@Data
@TableName("questions")
public class Question {

    @TableId(value = "id", type = IdType.AUTO)
    int id;

    int userId;
    String content;
    String title;
    Date time;

}
