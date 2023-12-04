package com.example.covbackend.user;

import com.example.covbackend.controller.UserController;
import com.example.covbackend.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * User相关的单元测试
 */
@SpringBootTest
@DisplayName("test User")
public class testUser extends Assertions{

    @Autowired
    UserService userService;
    @Autowired
    UserController userController;

    @Test
    @DisplayName("测试UserService")
    void testUserService(){

    }

    @Test
    @DisplayName("测试UserController")
    void testUserController(){

        //测试login功能
        /*assertAll("测试login()",
                () ->  assertEquals("登录成功",
                        userController.login("admin", "123456", null).getMsg(),
                        "登录成功测试失败"),

                () -> assertEquals("密码错误",
                        userController.login("admin", "654321", null).getMsg(),
                        "密码错误测试失败"),

                () -> assertEquals("用户名不存在",
                        userController.login("nimda", "123456", null).getMsg(),
                        "用户名不存在测试失败")
        );*/

    }
}
