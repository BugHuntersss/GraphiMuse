package com.example.covbackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.covbackend.entity.Station;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface StationMapper extends BaseMapper<Station> {
}
