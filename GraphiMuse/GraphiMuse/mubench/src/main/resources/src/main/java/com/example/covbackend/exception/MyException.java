package com.example.covbackend.exception;

import lombok.Getter;

@Getter
public class MyException extends Exception implements java.io.Serializable{

    private MyExceptionEnum exceptionEnum;

    private String msg;

    private String code;

    MyException(MyExceptionEnum exceptionEnum, String msg, String code){
        this.exceptionEnum = exceptionEnum;
        this.msg = msg;
        this.code = code;
    }
    MyException(MyExceptionEnum exceptionEnum){
        this.exceptionEnum = exceptionEnum;
        this.msg = "";
        this.code = "";
    }
    MyException(String msg, String code){
        this.exceptionEnum = MyExceptionEnum.DEFAULT_ERROR;
        this.msg = msg;
        this.code = code;
    }
    void printMsg(){
        System.out.println(this.msg);
    }

}
