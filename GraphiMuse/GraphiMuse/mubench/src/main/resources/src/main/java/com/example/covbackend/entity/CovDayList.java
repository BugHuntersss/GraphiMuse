package com.example.covbackend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 分省分国家每日数据
 */
@Data
@TableName("covdaylist")
public class CovDayList {
    @TableId(value = "id", type = IdType.AUTO)
    int id;

    String date;
    int y;
    int locationId;
    int confirm;
    int confirmAdd;
    int dead;
    int deadAdd;
    int heal;
    int healAdd;
}
