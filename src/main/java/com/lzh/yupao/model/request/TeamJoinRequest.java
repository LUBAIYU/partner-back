package com.lzh.yupao.model.request;

import lombok.Data;

@Data
public class TeamJoinRequest {

    /**
     * 队伍ID
     */
    private Long teamId;

    /**
     * 密码
     */
    private String password;
}
