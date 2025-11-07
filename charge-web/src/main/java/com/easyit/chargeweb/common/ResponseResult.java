package com.easyit.chargeweb.common;

import lombok.Data;

import java.io.Serializable;

/**
 * 统一响应结果类
 * 用于封装API响应数据
 */
@Data
public class ResponseResult<T> implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 响应状态码
     * 0: 成功
     * 非0: 失败
     */
    private int code;
    
    /**
     * 响应消息
     */
    private String message;
    
    /**
     * 响应数据
     */
    private T data;
    
    /**
     * 成功响应
     * @param data 响应数据
     * @return 响应结果
     */
    public static <T> ResponseResult<T> success(T data) {
        ResponseResult<T> result = new ResponseResult<>();
        result.setCode(0);
        result.setMessage("success");
        result.setData(data);
        return result;
    }
    
    /**
     * 失败响应
     * @param code 错误码
     * @param message 错误消息
     * @return 响应结果
     */
    public static <T> ResponseResult<T> fail(int code, String message) {
        ResponseResult<T> result = new ResponseResult<>();
        result.setCode(code);
        result.setMessage(message);
        return result;
    }
    
    /**
     * 失败响应（默认错误码）
     * @param message 错误消息
     * @return 响应结果
     */
    public static <T> ResponseResult<T> fail(String message) {
        return fail(1, message);
    }
}