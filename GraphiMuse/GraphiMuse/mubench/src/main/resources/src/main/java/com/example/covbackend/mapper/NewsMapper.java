package com.example.covbackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.covbackend.entity.News;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface NewsMapper extends BaseMapper<News> {
}
