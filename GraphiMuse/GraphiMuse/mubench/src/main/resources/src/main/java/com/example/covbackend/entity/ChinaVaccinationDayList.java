package com.example.covbackend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("chinavaccinationdaylist")
public class ChinaVaccinationDayList {
    @TableId(value = "id", type = IdType.AUTO)
    int id;

    String date;
    String y;

    int totalVaccinations;
    float totalVaccinationsPerHundred;

}
