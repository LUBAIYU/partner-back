package com.lzh.yupao.model.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户登录请求体
 *
 * @author lzh
 */
@Data
public class UserLoginRequest implements Serializable {
    private String userAccount;
    private String userPassword;
}
