package com.lzh.yupao.common;

/**
 * 结果工具类
 *
 * @author lzh
 */
public class ResultUtil {

    /**
     * 成功
     *
     * @param data
     * @param <T>
     * @return
     */
    public static <T> BaseResponse<T> success(T data) {
        return new BaseResponse<>(0, data, "ok");
    }

    /**
     * 失败
     *
     * @param errorCode
     * @return
     */
    public static BaseResponse error(ErrorCode errorCode) {
        return new BaseResponse<>(errorCode);
    }


    /**
     * 失败
     *
     * @param errorCode
     * @param description
     * @return
     */
    public static BaseResponse error(ErrorCode errorCode, String description) {
        return new BaseResponse<>(errorCode.getCode(), null, errorCode.getMessage(), description);
    }

    /**
     * 失败
     *
     * @param errorCode
     * @param msg
     * @param description
     * @return
     */
    public static BaseResponse error(ErrorCode errorCode, String msg, String description) {
        return new BaseResponse<>(errorCode.getCode(), null, msg, description);
    }

    /**
     * 失败
     *
     * @param code
     * @param msg
     * @param description
     * @return
     */
    public static BaseResponse error(Integer code, String msg, String description) {
        return new BaseResponse<>(code, null, msg, description);
    }
}
