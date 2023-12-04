package com.example.covbackend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("nialist")
public class NiaList {
    @TableId(value = "id", type = IdType.AUTO)
    int id;

    String name;
    String content;
    Date time;
}
