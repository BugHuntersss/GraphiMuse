package com.example.covbackend.mapper;
import com.example.covbackend.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.*;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 */

@Mapper
public interface UserMapper extends BaseMapper<User> {

    @Select("select * from users where id = #{id}")
    User getUserById(@Param("id") int id);

    @Select("select * from users where username = #{username}")
    User getUserByUsername(@Param("username") String username);

    @Select("select * from users where mail = #{mail}")
    User getUserByMail(@Param("mail") String mail);
}
