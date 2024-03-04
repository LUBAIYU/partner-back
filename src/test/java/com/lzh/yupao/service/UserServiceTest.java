package com.lzh.yupao.service;

import com.lzh.yupao.model.domain.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

@SpringBootTest
class UserServiceTest {

    @Resource
    private UserService userService;

    @Test
    void testAddUser() {
        User user = new User();
        user.setUsername("123");
        user.setUserAccount("admin");
        user.setAvatarUrl("123");
        user.setGender(0);
        user.setUserPassword("admin");
        user.setPhone("123");
        user.setEmail("123");
        boolean res = userService.save(user);
        System.out.println(user.getId());
        Assertions.assertTrue(res);
    }

    @Test
    void userRegister() {
        String userAccount = "abc abc";
        String userPassword = "123456789";
        String checkPassword = "123456789";
        String planetCode = "12344";
        Long res = userService.userRegister(userAccount, userPassword, checkPassword, planetCode);
        Assertions.assertTrue(res > 0);
    }

    @Test
    void testSearchUsersByTags() {
        List<String> tagNameList = Arrays.asList("java", "c++", "python");
        List<User> userList = userService.searchUsersByTags(tagNameList);
        Assertions.assertTrue(!userList.isEmpty());
    }
}