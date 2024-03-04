package com.lzh.yupao.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lzh.yupao.model.domain.UserTeam;
import com.lzh.yupao.service.UserTeamService;
import com.lzh.yupao.mapper.UserTeamMapper;
import org.springframework.stereotype.Service;

/**
* @author Administrator
* @description 针对表【user_team(用户队伍关系)】的数据库操作Service实现
* @createDate 2024-02-26 20:35:14
*/
@Service
public class UserTeamServiceImpl extends ServiceImpl<UserTeamMapper, UserTeam>
    implements UserTeamService{

}




