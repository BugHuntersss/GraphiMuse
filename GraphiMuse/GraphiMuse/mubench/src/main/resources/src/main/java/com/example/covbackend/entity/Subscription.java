package com.example.covbackend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("subscriptions")
public class Subscription {

    @TableId(value = "id", type = IdType.AUTO)
    int id;

    int userId;
    int locId;

}
