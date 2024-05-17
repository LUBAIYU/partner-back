package com.lzh.yupao.service;

import com.lzh.yupao.utils.AlgorithmUtil;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;

@SpringBootTest
public class AlgorithmTest {

    @Test
    void test() {
        List<String> list1 = Arrays.asList("java", "大一", "男");
        List<String> list2 = Arrays.asList("java", "大一", "女");
        List<String> list3 = Arrays.asList("java", "大二", "女");
        System.out.println(AlgorithmUtil.minDistance(list1, list2));
        System.out.println(AlgorithmUtil.minDistance(list1, list3));
    }
}
