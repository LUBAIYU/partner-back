package com.lzh.yupao.task;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lzh.yupao.model.domain.User;
import com.lzh.yupao.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
@Slf4j
public class PreCacheUser {

    @Resource
    private UserService userService;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Resource
    private RedissonClient redissonClient;

    //主要用户
    private final List<Long> mainUserList = Arrays.asList(2L);

    //这里的cron表达式指每天0点0分0秒执行
    @Scheduled(cron = "0 0 0 * * ? ")
    public void doRreCache() {
        RLock lock = redissonClient.getLock("yupao:precache:docache:lock");
        try {
            //只有一个线程能获取到锁
            if (lock.tryLock(0, 30000, TimeUnit.MILLISECONDS)) {
                ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
                QueryWrapper<User> wrapper = new QueryWrapper<>();
                List<User> userList;
                for (Long userId : mainUserList) {
                    String key = String.format("yupao:user:recommend:%s", userId);
                    Page<User> p = userService.page(new Page<>(1, 20), wrapper);
                    userList = p.getRecords().stream().map(user -> userService.getSafetyUser(user)).collect(Collectors.toList());
                    try {
                        valueOperations.set(key, userList, 1, TimeUnit.MINUTES);
                    } catch (Exception e) {
                        log.error("redis set key error", e);
                    }
                }
            }
        } catch (InterruptedException e) {
            log.error("redis lock error", e);
        } finally {
            //如果当前的锁是当前线程加的锁，当前线程才可释放锁，即只能释放自己的锁
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }
}
