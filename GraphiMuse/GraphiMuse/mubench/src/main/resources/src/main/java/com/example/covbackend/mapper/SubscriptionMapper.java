package com.example.covbackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.covbackend.entity.Subscription;
import com.example.covbackend.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface SubscriptionMapper extends BaseMapper<Subscription> {
    @Select("select loc_id from subscriptions where user_id=#{user_id}")
    int[] getSubscriptionByUserId(@Param("user_id") int userid);

}
