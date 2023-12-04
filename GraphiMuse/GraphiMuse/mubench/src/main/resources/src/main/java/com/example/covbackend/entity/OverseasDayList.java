package com.example.covbackend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("overseasdaylist")
public class OverseasDayList {
    @TableId(value = "id", type = IdType.AUTO)
    int id;

    String date;
    int y;
    int confirm;
    int ConfirmAdd;
    int dead;
    int heal;
    float healRate;
    float deadRate;
}
