package com.lzh.yupao.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lzh.yupao.common.ErrorCode;
import com.lzh.yupao.enums.TeamStatusEnum;
import com.lzh.yupao.exception.BusinessException;
import com.lzh.yupao.mapper.TeamMapper;
import com.lzh.yupao.model.domain.Team;
import com.lzh.yupao.model.domain.User;
import com.lzh.yupao.model.domain.UserTeam;
import com.lzh.yupao.model.dto.TeamQuery;
import com.lzh.yupao.model.request.TeamJoinRequest;
import com.lzh.yupao.model.request.TeamQuitRequest;
import com.lzh.yupao.model.request.TeamUpdateRequest;
import com.lzh.yupao.model.vo.TeamUserVo;
import com.lzh.yupao.model.vo.UserVo;
import com.lzh.yupao.service.TeamService;
import com.lzh.yupao.service.UserService;
import com.lzh.yupao.service.UserTeamService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * @author Administrator
 * @description 针对表【team(队伍)】的数据库操作Service实现
 * @createDate 2024-02-26 20:34:59
 */
@Service
public class TeamServiceImpl extends ServiceImpl<TeamMapper, Team>
        implements TeamService {

    @Resource
    private UserService userService;
    @Resource
    private UserTeamService userTeamService;

    @Override
    @Transactional
    public Long addTeam(Team team, User loginUser) {
        //1. 队伍人数 > 1 且 <= 20
        Integer maxNum = team.getMaxNum();
        maxNum = Optional.ofNullable(maxNum).orElse(0);
        if (maxNum < 1 || maxNum > 20) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍人数不满足要求");
        }
        //2. 队伍标题 <= 20
        String name = team.getName();
        if (StringUtils.isBlank(name) || name.length() > 20) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍标题不满足要求");
        }
        //3. 描述 <= 512
        String description = team.getDescription();
        if (StringUtils.isNotBlank(description) && description.length() > 512) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍描述不满足要求");
        }
        //4. status 是否公开（int）不传默认为 0（公开）
        Integer status = Optional.ofNullable(team.getStatus()).orElse(0);
        TeamStatusEnum statusEnum = TeamStatusEnum.getEnumByStatus(status);
        if (statusEnum == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍状态不满足要求");
        }
        //5. 如果 status 是加密状态，一定要有密码，且密码 <= 32
        if (TeamStatusEnum.SECRET.equals(statusEnum)) {
            String password = team.getPassword();
            if (StringUtils.isBlank(password) || password.length() > 32) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码不满足要求");
            }
        }
        //6. 超时时间 > 当前时间
        Date expireTime = team.getExpireTime();
        if (expireTime != null && expireTime.before(new Date())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "超时时间不满足要求");
        }
        //7. 校验用户最多创建 5 个队伍
        Long userId = loginUser.getId();
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", userId);
        long count = this.count(queryWrapper);
        if (count >= 5) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户最多创建5个队伍");
        }
        //8.插入队伍信息到队伍表
        team.setId(null);
        team.setUserId(userId);
        boolean result = this.save(team);
        if (!result) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "创建队伍失败");
        }
        //9. 插入用户  => 队伍关系到关系表
        UserTeam userTeam = new UserTeam();
        userTeam.setUserId(userId);
        userTeam.setTeamId(team.getId());
        userTeam.setJoinTime(new Date());
        result = userTeamService.save(userTeam);
        if (!result) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "创建队伍失败");
        }
        return team.getId();
    }

    @Override
    public List<TeamUserVo> listTeams(TeamQuery teamQuery, Boolean isAdmin) {
        //1. 添加查询条件
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>();
        if (teamQuery != null) {
            //根据ID查询
            Long id = teamQuery.getId();
            if (id != null && id > 0) {
                queryWrapper.eq("id", id);
            }
            //根据ID列表查询
            List<Long> idList = teamQuery.getIdList();
            if (!CollectionUtils.isEmpty(idList)) {
                queryWrapper.in("id", idList);
            }
            //根据队伍名称查询
            String name = teamQuery.getName();
            if (StringUtils.isNotBlank(name)) {
                queryWrapper.like("name", name);
            }
            //根据队伍描述查询
            String description = teamQuery.getDescription();
            if (StringUtils.isNotBlank(description)) {
                queryWrapper.like("description", description);
            }
            //根据关键词查询
            String searchText = teamQuery.getSearchText();
            if (StringUtils.isNotBlank(searchText)) {
                queryWrapper.and(qw -> qw.like("name", searchText).or().like("description", searchText));
            }
            //根据队伍最大人数查询
            Integer maxNum = teamQuery.getMaxNum();
            if (maxNum != null && maxNum > 0) {
                queryWrapper.eq("maxNum", maxNum);
            }
            //根据队伍创建人查询
            Long userId = teamQuery.getUserId();
            if (userId != null && userId > 0) {
                queryWrapper.eq("userId", userId);
            }
            //根据状态查询
            //只有管理员才能查看加密还有私有的队伍
            Integer status = teamQuery.getStatus();
            TeamStatusEnum statusEnum = TeamStatusEnum.getEnumByStatus(status);
            if (statusEnum == null) {
                statusEnum = TeamStatusEnum.PUBLIC;
            }
            if (!isAdmin && !statusEnum.equals(TeamStatusEnum.PUBLIC)) {
                throw new BusinessException(ErrorCode.NO_AUTH);
            }
            queryWrapper.eq("status", statusEnum.getStatus());
        }
        //只展示未过期的队伍或者过期时间为空的队伍
        queryWrapper.and(qw -> qw.gt("expireTime", new Date()).or().isNull("expireTime"));
        List<Team> teamList = this.list(queryWrapper);
        if (CollectionUtils.isEmpty(teamList)) {
            return new ArrayList<>();
        }
        List<TeamUserVo> teamUserVoList = new ArrayList<>();
        for (Team team : teamList) {
            Long userId = team.getUserId();
            if (userId == null) {
                continue;
            }
            User user = userService.getById(userId);
            TeamUserVo teamUserVo = new TeamUserVo();
            BeanUtils.copyProperties(team, teamUserVo);
            //脱敏用户信息
            if (user != null) {
                UserVo userVo = new UserVo();
                BeanUtils.copyProperties(user, userVo);
                teamUserVo.setCreateUser(userVo);
            }
            teamUserVoList.add(teamUserVo);
        }
        return teamUserVoList;
    }

    @Override
    public boolean updateTeam(TeamUpdateRequest teamUpdateRequest, User loginUser) {
        //1. 查询队伍是否存在
        Long teamId = teamUpdateRequest.getId();
        if (teamId == null || teamId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "id不能为空");
        }
        Team team = this.getById(teamId);
        if (team == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "队伍不存在");
        }
        //2. 只有管理员或者队伍的创建者可以修改
        if (!loginUser.getId().equals(team.getUserId()) && !userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        //3. 如果队伍状态改为加密，必须要有密码
        Integer status = teamUpdateRequest.getStatus();
        TeamStatusEnum statusEnum = TeamStatusEnum.getEnumByStatus(status);
        if (statusEnum != null && statusEnum.equals(TeamStatusEnum.SECRET)) {
            String password = teamUpdateRequest.getPassword();
            if (StringUtils.isBlank(password)) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "加密状态，必须有密码");
            }
        }
        Team updateTeam = new Team();
        BeanUtils.copyProperties(teamUpdateRequest, updateTeam);
        return this.updateById(updateTeam);
    }

    @Override
    public Boolean joinTeam(TeamJoinRequest teamJoinRequest, User loginUser) {
        //1. 用户最多加入 5 个队伍
        Long userId = loginUser.getId();
        QueryWrapper<UserTeam> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", userId);
        long count = userTeamService.count(queryWrapper);
        if (count >= 5) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户最多加入5个队伍");
        }
        //2. 队伍必须存在，只能加入未满、未过期的队伍
        Long teamId = teamJoinRequest.getTeamId();
        if (teamId == null || teamId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Team team = this.getById(teamId);
        if (team == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "队伍不存在");
        }
        Integer maxNum = team.getMaxNum();
        queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("teamId", teamId);
        count = userTeamService.count(queryWrapper);
        if (count == maxNum) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍人数已满");
        }
        Date expireTime = team.getExpireTime();
        if (expireTime != null && expireTime.before(new Date())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍已过期");
        }
        //3. 不能加入自己的队伍，不能重复加入已加入的队伍
        if (userId.equals(team.getUserId())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "不能加入自己的队伍");
        }
        queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", userId);
        queryWrapper.eq("teamId", teamId);
        count = userTeamService.count(queryWrapper);
        if (count > 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "已经加入该队伍");
        }
        //4. 禁止加入私有的队伍
        Integer status = team.getStatus();
        TeamStatusEnum statusEnum = TeamStatusEnum.getEnumByStatus(status);
        if (statusEnum != null && statusEnum.equals(TeamStatusEnum.PRIVATE)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "禁止加入私有的队伍");
        }
        //5. 如果加入的队伍是加密的，必须密码匹配才可以
        if (TeamStatusEnum.SECRET.equals(statusEnum)) {
            String password = teamJoinRequest.getPassword();
            if (!team.getPassword().equals(password)) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码不匹配");
            }
        }
        //6. 新增队伍 - 用户关联信息
        UserTeam userTeam = new UserTeam();
        userTeam.setUserId(userId);
        userTeam.setTeamId(teamId);
        userTeam.setJoinTime(new Date());
        return userTeamService.save(userTeam);
    }

    @Override
    @Transactional
    public Boolean quitTeam(TeamQuitRequest teamQuitRequest, User loginUser) {
        //1. 校验队伍是否存在
        Long teamId = teamQuitRequest.getTeamId();
        if (teamId == null || teamId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Team team = this.getById(teamId);
        if (team == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "队伍不存在");
        }
        //2. 校验我是否已加入队伍
        Long userId = loginUser.getId();
        QueryWrapper<UserTeam> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", userId);
        queryWrapper.eq("teamId", teamId);
        long count = userTeamService.count(queryWrapper);
        if (count == 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "未加入该队伍");
        }
        //3. 判断逻辑
        //3.1 如果队伍只剩一人，队伍解散
        queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("teamId", teamId);
        long teamHasJoinCount = userTeamService.count(queryWrapper);
        if (teamHasJoinCount == 1) {
            //删除队伍信息
            this.removeById(teamId);
        } else {
            //3.2 还有其他人 ，如果是队长退出队伍，权限转移给第二早加入的用户
            //是否为队长
            if (userId.equals(team.getUserId())) {
                //只查两条数据
                queryWrapper.last("order by id asc limit 2");
                List<UserTeam> userTeamList = userTeamService.list(queryWrapper);
                if (CollectionUtils.isEmpty(userTeamList) || userTeamList.size() < 2) {
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR);
                }
                UserTeam nextUserTeam = userTeamList.get(1);
                Long nextTeamLeaderId = nextUserTeam.getUserId();
                Team updateTeam = new Team();
                updateTeam.setId(teamId);
                updateTeam.setUserId(nextTeamLeaderId);
                boolean res = this.updateById(updateTeam);
                if (!res) {
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR, "更新队伍队长失败");
                }
                queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("teamId", teamId);
                queryWrapper.eq("userId", userId);
            } else {
                //3.3 非队长，自己退出队伍
                queryWrapper.eq("userId", userId);
            }
        }
        return userTeamService.remove(queryWrapper);
    }

    @Override
    @Transactional
    public Boolean delTeam(Long teamId, User loginUser) {
        //1. 校验队伍是否存在
        Team team = this.getById(teamId);
        if (team == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "队伍不存在");
        }
        //2. 校验你是不是队伍的队长
        Long userId = loginUser.getId();
        if (!userId.equals(team.getUserId())) {
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        //3. 移除所有加入队伍的关联信息
        QueryWrapper<UserTeam> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("teamId", teamId);
        boolean res = userTeamService.remove(queryWrapper);
        if (!res) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "解散队伍失败");
        }
        //4. 删除队伍
        return this.removeById(teamId);
    }
}




