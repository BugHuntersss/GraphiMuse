package com.example.covbackend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.covbackend.entity.Subscription;
import com.example.covbackend.mapper.LocationMapper;
import com.example.covbackend.mapper.SubscriptionMapper;
import com.example.covbackend.service.SubscriptionService;


import org.springframework.stereotype.Service;
import javax.annotation.Resource;


@Service
public class SubscriptionServiceImpl extends ServiceImpl<SubscriptionMapper, Subscription> implements SubscriptionService {
    @Resource
    SubscriptionMapper subscriptionMapper;
    @Resource
    LocationMapper locationMapper;

    public void insert(int user_id,int loc_id){
        Subscription subscription = new Subscription();
        subscription.setUserId(user_id);
        subscription.setLocId(loc_id);
        subscriptionMapper.insert(subscription);
    }

    public int[] getLocationIdByUserId(int user_id){
        int[] locIdList = subscriptionMapper.getSubscriptionByUserId(user_id);
        return locIdList;
    }
}
