package com.example.covbackend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 新闻表
 */
@Data
@TableName("news")
public class News {

    @TableId(value = "id", type = IdType.AUTO)
    int id;

    String title;
    String url;
    String site;
    Date time;
    int locationId;
}
