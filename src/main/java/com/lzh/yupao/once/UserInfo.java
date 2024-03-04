package com.lzh.yupao.once;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
public class UserInfo {
    /**
     * 星球编号
     */
    @ExcelProperty("成员编号")
    private String planetCode;
    /**
     * 用户昵称
     */
    @ExcelProperty("成员昵称")
    private String username;
    /**
     * 本月积分
     */
    @ExcelProperty("本月积分")
    private Long point;
}
