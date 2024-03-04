package com.lzh.yupao.service;

import com.lzh.yupao.model.domain.User;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.StopWatch;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@SpringBootTest
public class InsertUserTest {

    @Resource
    private UserService userService;

    private ExecutorService executorService = new ThreadPoolExecutor(10, 1000, 10000, TimeUnit.MINUTES, new ArrayBlockingQueue<>(10000));

    @Test
    void testInsertUser() {
        StopWatch watch = new StopWatch();
        watch.start();
        List<CompletableFuture<Void>> futureList = new ArrayList<>();
        int j = 0;
        for (int i = 0; i < 10; i++) {
            List<User> userList = new ArrayList<>();
            while (true) {
                j++;
                User user = new User();
                user.setUsername("lzf");
                user.setUserAccount("lzf");
                user.setUserPassword("123456");
                user.setAvatarUrl("https://www.codefather.cn/logo.png");
                user.setGender(0);
                user.setProfile("我是lzf");
                user.setPhone("123456");
                user.setEmail("123456");
                user.setUserStatus(0);
                user.setIsDelete(0);
                user.setUserRole(0);
                user.setPlanetCode("123");
                user.setTags("[]");
                userList.add(user);
                if (j % 10 == 0) {
                    break;
                }
            }
            //创建一个异步任务实现多线程写入
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                System.out.println(Thread.currentThread().getName());
                userService.saveBatch(userList, 10);
            }, executorService);
            futureList.add(future);
        }
        //等待所有线程完成写入操作
        CompletableFuture.allOf(futureList.toArray(new CompletableFuture[] {})).join();
        watch.stop();
        System.out.println(watch.getTotalTimeMillis());
    }
}
