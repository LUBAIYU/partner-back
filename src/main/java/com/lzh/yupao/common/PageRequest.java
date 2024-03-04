package com.lzh.yupao.common;

import lombok.Data;

/**
 * 分页参数
 */
@Data
public class PageRequest {
    /**
     * 当前页数
     */
    private Integer page = 1;
    /**
     * 每页记录数
     */
    private Integer pageSize = 10;
}
