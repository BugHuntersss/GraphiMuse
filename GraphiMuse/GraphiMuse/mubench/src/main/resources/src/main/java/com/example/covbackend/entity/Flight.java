package com.example.covbackend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("flights")
public class Flight {

    @TableId(value = "id", type = IdType.AUTO)
    int id;
    String flightNo;
    String fromAirport;
    String toAirport;
    String stop;
    Date date;
    String company;
}
