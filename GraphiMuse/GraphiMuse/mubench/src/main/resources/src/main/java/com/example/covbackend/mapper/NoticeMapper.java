package com.example.covbackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.covbackend.entity.Notice;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface NoticeMapper extends BaseMapper<Notice> {
}
