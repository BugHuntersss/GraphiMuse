package com.example.covbackend.entity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;


import java.io.Serializable;

/**
 * 用户表
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("users")
public class User implements Serializable {
    @TableId(value = "id", type = IdType.AUTO)
    private int Id;

    private String username;
    private String password;
    private String mail;
    private boolean is_authority;
    private int location_id;
    private String introduction;
}
