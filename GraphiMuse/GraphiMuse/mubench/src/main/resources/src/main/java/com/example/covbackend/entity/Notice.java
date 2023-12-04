package com.example.covbackend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import javax.persistence.Table;
import java.util.Date;

/**
 * 官方通告表
 */
@Data
@TableName("notices")
public class Notice {

    @TableId(value = "id", type = IdType.AUTO)
    int id;

    String title;
    String url;
    Date time;
}
