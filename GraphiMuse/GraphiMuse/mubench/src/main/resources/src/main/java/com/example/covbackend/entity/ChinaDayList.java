package com.example.covbackend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 中国每日数据表
 */
@Data
@TableName("chinadaylist")
public class ChinaDayList {

    @TableId(value = "id", type = IdType.AUTO)
    int id;

    String date;
    int y;
    int confirm;
    int nowConfirm;
    int dead;
    int heal;
    int nowSevere;
    int suspect;
    float deadRate;
    float healRate;
    int noInfect;
    int noInfectH5;
    int localConfirm;
    int localConfirmH5;
    int importedCase;

}
