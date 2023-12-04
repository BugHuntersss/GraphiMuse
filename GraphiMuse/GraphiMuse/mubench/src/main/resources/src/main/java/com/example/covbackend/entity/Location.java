package com.example.covbackend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 地区表
 */
@Data
@TableName("locations")
public class Location {

    @TableId(value = "id", type = IdType.AUTO)
    int id;

    int belongId;
    String name;
    String type;
    int risk;

}
