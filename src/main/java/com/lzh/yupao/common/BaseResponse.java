package com.lzh.yupao.common;

import lombok.Data;

/**
 * 统一返回结果
 *
 * @param <T>
 * @author lzh
 */
@Data
public class BaseResponse<T> {

    private Integer code;
    private T data;
    private String msg;
    private String description;

    public BaseResponse(Integer code, T data, String msg, String description) {
        this.code = code;
        this.data = data;
        this.msg = msg;
        this.description = description;
    }

    public BaseResponse(Integer code, T data) {
        this(code, data, "", "");
    }

    public BaseResponse(Integer code, T data, String msg) {
        this(code, data, msg, "");
    }

    public BaseResponse(ErrorCode errorCode) {
        this(errorCode.getCode(), null, errorCode.getMessage(), errorCode.getDescription());
    }
}
