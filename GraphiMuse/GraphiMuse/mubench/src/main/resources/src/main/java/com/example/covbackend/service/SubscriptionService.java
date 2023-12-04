package com.example.covbackend.service;

import com.example.covbackend.entity.Location;
import com.example.covbackend.entity.Subscription;
import com.example.covbackend.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.covbackend.mapper.UserMapper;

import java.util.List;

public interface SubscriptionService extends IService<Subscription> {
    void insert(int user_id,int loc_id);

    int[] getLocationIdByUserId(int user_id);
}
