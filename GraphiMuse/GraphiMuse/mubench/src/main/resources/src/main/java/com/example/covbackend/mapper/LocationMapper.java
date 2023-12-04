package com.example.covbackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.covbackend.entity.Location;
import com.example.covbackend.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface LocationMapper  extends BaseMapper<Location> {

}

