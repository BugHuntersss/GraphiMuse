package com.example.covbackend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("trains")
public class Train {

    @TableId(value = "id", type = IdType.AUTO)
    int id;

    String trainNo;
    String fromStation;
    String toStation;
    String stops;
    Date date;
    int totalNum;
}
