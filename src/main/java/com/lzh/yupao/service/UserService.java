package com.lzh.yupao.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lzh.yupao.model.domain.User;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author lzh
 * @description 针对表【user(用户)】的数据库操作Service
 * @createDate 2024-02-05 11:01:55
 */
public interface UserService extends IService<User> {

    /**
     * 用户注册
     *
     * @param userAccount   用户账户
     * @param userPassword  用户密码
     * @param checkPassword 校验密码
     * @param planetCode    星球编号
     * @return 新用户ID
     */
    Long userRegister(String userAccount, String userPassword, String checkPassword, String planetCode);

    /**
     * 用户登录
     *
     * @param userAccount  用户账户
     * @param userPassword 用户密码
     * @return 脱敏后的用户信息
     */
    User userLogin(String userAccount, String userPassword, HttpServletRequest request);

    /**
     * 用户脱敏
     *
     * @param originUser
     * @return
     */
    User getSafetyUser(User originUser);

    /**
     * 用户注销
     *
     * @param request
     * @return
     */
    Integer userLogout(HttpServletRequest request);

    /**
     * 根据标签查询用户
     *
     * @param tagNameList
     * @return
     */
    List<User> searchUsersByTags(List<String> tagNameList);

    /**
     * 是否为管理员
     *
     * @param request
     * @return
     */
    Boolean isAdmin(HttpServletRequest request);

    Boolean isAdmin(User loginUser);

    /**
     * 获取登录用户
     *
     * @param request
     * @return
     */
    User getLoginUser(HttpServletRequest request);

    /**
     * 修改用户信息
     *
     * @param user
     * @param loginUser
     * @return
     */
    Integer updateUser(User user, User loginUser);

    /**
     * 根据标签匹配用户并返回对应数据的数据
     *
     * @param num
     * @param loginUser
     * @return
     */
    List<User> matchUsers(long num, User loginUser);
}
