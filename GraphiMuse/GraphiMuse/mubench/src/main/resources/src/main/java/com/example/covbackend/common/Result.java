package com.example.covbackend.common;
import com.baomidou.mybatisplus.extension.api.R;
import com.example.covbackend.exception.MyException;
import lombok.Data;

import java.io.Serializable;

@Data
public class Result implements Serializable {
    private String code;
    private String msg;
    private Object data;
    public static Result succ(Object data) {
        Result m = new Result();
        m.setCode("200");
        m.setData(data);
        m.setMsg("操作成功");
        return m;
    }
    public static Result succ(String mess, Object data) {
        Result m = new Result();
        m.setCode("200");
        m.setData(data);
        m.setMsg(mess);
        return m;
    }
    public static Result succ() {
        Result m = new Result();
        m.setCode("200");
        m.setData(null);
        m.setMsg("操作成功");
        return m;
    }
    public static Result noalertsucc(Object data) {
        Result m = new Result();
        m.setCode("201");
        m.setData(data);
        m.setMsg("操作成功");
        return m;
    }
    public static Result noalertsucc(String mess, Object data) {
        Result m = new Result();
        m.setCode("201");
        m.setData(data);
        m.setMsg(mess);
        return m;
    }
    public static Result fail(String mess) {
        Result m = new Result();
        m.setCode("400");
        m.setData(null);
        m.setMsg(mess);
        return m;
    }
    public static Result fail(String mess, Object data) {
        Result m = new Result();
        m.setCode("400");
        m.setData(data);
        m.setMsg(mess);
        return m;
    }

    public static Result succ( String code ,String mess, Object data) {
        Result m = new Result();
        m.setCode(code);
        m.setData(data);
        m.setMsg(mess);
        return m;
    }
    public static Result fail(MyException exception){
        Result m = new Result();
        m.setCode(exception.getCode());
        m.setData(null);
        m.setMsg(exception.getMsg());
        return m;
    }
    public static Result fail(MyException exception, Object data){
        Result m = new Result();
        m.setCode(exception.getCode());
        m.setMsg(exception.getMsg());
        m.setData(data);
        return m;
    }
}
