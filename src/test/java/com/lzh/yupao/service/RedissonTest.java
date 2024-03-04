package com.lzh.yupao.service;

import org.junit.jupiter.api.Test;
import org.redisson.api.RList;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

@SpringBootTest
public class RedissonTest {

    @Resource
    private RedissonClient redissonClient;

    @Test
    void test() {
        RList<String> list = redissonClient.getList("test");
        list.add("lzf");
        System.out.println(list.get(0));
    }

    @Test
    void doCache() {
        RLock lock = redissonClient.getLock("yupao:precache:docache:lock");
        try {
            //只有一个线程能获取到锁
            if (lock.tryLock(0, -1, TimeUnit.MILLISECONDS)) {

            }
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        } finally {
            //如果当前的锁是当前线程加的锁，当前线程才可释放锁，即只能释放自己的锁
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }
}
