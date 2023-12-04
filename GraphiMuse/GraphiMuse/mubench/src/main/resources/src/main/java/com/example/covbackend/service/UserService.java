package com.example.covbackend.service;

import com.example.covbackend.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.covbackend.mapper.UserMapper;

public interface UserService extends IService<User>{
    void insert(User user);
    User getUserById(int id);
    User getUserByUsername(String username);
    Boolean duplicatedMail(String mail);
    User getUserByMail(String mail);
}
