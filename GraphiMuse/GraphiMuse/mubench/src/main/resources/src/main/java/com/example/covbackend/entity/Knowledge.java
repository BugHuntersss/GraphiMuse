package com.example.covbackend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 小知识
 */
@Data
@TableName("knowledge")
public class Knowledge {

    @TableId(value = "id", type = IdType.AUTO)
    int id;

    String title;
    String description;
    String content;
    String imgurl;
}
