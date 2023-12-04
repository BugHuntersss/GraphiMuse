package com.example.covbackend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.sql.Date;

@Data
@TableName("covdata")
public class CovData {

    @TableId(value = "id", type = IdType.AUTO)
    int id;

    int locationId;
    int confirm;
    int confirmAdd;
    int nowConfirm;
    int nowConfirmAdd;
    int dead;
    int deadAdd;
    int heal;
    int healAdd;
    int noInfect;
    int noInfectAdd;
    long totalVaccinations;
    float totalVaccinationsPerHundred;
    Date time;
}
