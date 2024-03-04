package com.lzh.yupao;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan("com.lzh.yupao.mapper")
@EnableScheduling
public class YupaoBackApplication {

    public static void main(String[] args) {
        SpringApplication.run(YupaoBackApplication.class, args);
    }

}
