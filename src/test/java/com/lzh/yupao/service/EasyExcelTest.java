package com.lzh.yupao.service;

import com.alibaba.excel.EasyExcel;
import com.lzh.yupao.once.UserInfo;
import com.lzh.yupao.once.UserInfoListener;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class EasyExcelTest {

    String fileName = "E:\\知识星球\\用户中心\\代码\\后端\\user-center\\src\\main\\resources\\testExcel.xlsx";

    /**
     * 使用监听器读取文件
     */
    @Test
    void testReaderListener() {
        EasyExcel.read(fileName, UserInfo.class, new UserInfoListener()).sheet().doRead();
    }

    /**
     * 使用同步读取文件
     */
    @Test
    void testSyncReadExcel() {
        List<UserInfo> list = EasyExcel.read(fileName).head(UserInfo.class).sheet().doReadSync();
        for (UserInfo userInfo : list) {
            System.out.println(userInfo.getPlanetCode() + "," + userInfo.getUsername() + "," + userInfo.getPoint());
        }
    }
}