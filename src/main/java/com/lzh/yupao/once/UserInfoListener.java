package com.lzh.yupao.once;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;

public class UserInfoListener implements ReadListener<UserInfo> {
    /**
     * 这个每一条数据解析都会来调用
     *
     * @param userInfo
     * @param analysisContext
     */
    @Override
    public void invoke(UserInfo userInfo, AnalysisContext analysisContext) {
        System.out.println(userInfo.getPlanetCode() + "," + userInfo.getUsername() + "," + userInfo.getPoint());
    }

    /**
     * 所有数据解析完成了 都会来调用
     *
     * @param analysisContext
     */
    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        System.out.println("已完成解析");
    }
}
