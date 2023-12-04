package com.example.covbackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.covbackend.entity.Knowledge;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface KnowledgeMapper extends BaseMapper<Knowledge> {
}
