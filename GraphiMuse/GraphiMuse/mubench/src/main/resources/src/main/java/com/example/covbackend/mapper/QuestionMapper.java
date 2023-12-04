package com.example.covbackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.covbackend.entity.Location;
import com.example.covbackend.entity.Question;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface QuestionMapper  extends BaseMapper<Question> {
    @Select("select * from questions where user_id=#{userid}")
    List<Question> getQuestionByUserId(@Param("userid") int userid);
}
