package com.lzh.yupao.enums;

import lombok.Getter;

/**
 * 队伍状态枚举
 */
@Getter
public enum TeamStatusEnum {

    PUBLIC(0, "公开"),
    PRIVATE(1, "私有"),
    SECRET(2, "加密");

    private Integer status;
    private String text;

    TeamStatusEnum(Integer status, String text) {
        this.status = status;
        this.text = text;
    }

    public static TeamStatusEnum getEnumByStatus(Integer status) {
        if (status == null) {
            return null;
        }
        TeamStatusEnum[] values = TeamStatusEnum.values();
        for (TeamStatusEnum teamStatusEnum : values) {
            if (status.equals(teamStatusEnum.getStatus())) {
                return teamStatusEnum;
            }
        }
        return null;
    }
}
