package com.atguigu.common.result;

import lombok.Data;

@Data
public class Result<T> {
    private Integer code;   //状态码
    private String message; //返回信息
    private T data;         //数据

    private Result(){}

    //封装返回数据
    public static<T> Result<T> build(T body, ResultCodeEnum resultCodeEnum){
        Result<T> result = new Result<>();
        if(body != null){
            result.setData(body);
        }
        result.setCode(resultCodeEnum.getCode());
        result.setMessage(result.getMessage());
        return result;
    }

    //成功 -------------------------
    public static<T> Result<T> ok(){
        return build(null,ResultCodeEnum.SUCCESS);
    }

    public static<T> Result<T> ok(T data){
        return build(data, ResultCodeEnum.SUCCESS);
    }

    //失败 -------------------------
    public static<T> Result<T> fail(){
        return build(null, ResultCodeEnum.FAIL);
    }

    public static<T> Result<T> fail(T data){
        return build(data, ResultCodeEnum.FAIL);
    }

    //重新设置message -------------------------
    public Result<T> message(String msg){
        this.setMessage(msg);
        return this;
    }
    //重新设置code -------------------------
    public Result<T> code(Integer code){
        this.setCode(code);
        return this;
    }

}
