package com.lzh.yupao.model.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户退出队伍请求体
 */
@Data
public class TeamQuitRequest implements Serializable {
    /**
     * 队伍ID
     */
    private Long teamId;
}
