package com.lzh.yupao.service;

import com.lzh.yupao.model.domain.Team;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lzh.yupao.model.domain.User;
import com.lzh.yupao.model.dto.TeamQuery;
import com.lzh.yupao.model.request.TeamAddRequest;
import com.lzh.yupao.model.request.TeamJoinRequest;
import com.lzh.yupao.model.request.TeamQuitRequest;
import com.lzh.yupao.model.request.TeamUpdateRequest;
import com.lzh.yupao.model.vo.TeamUserVo;

import java.util.List;

/**
 * @author Administrator
 * @description 针对表【team(队伍)】的数据库操作Service
 * @createDate 2024-02-26 20:34:59
 */
public interface TeamService extends IService<Team> {

    /**
     * 创建队伍
     *
     * @param team
     * @param loginUser
     * @return
     */
    Long addTeam(Team team, User loginUser);

    /**
     * 查询队伍列表
     *
     * @param teamQuery
     * @param isAdmin
     * @return
     */
    List<TeamUserVo> listTeams(TeamQuery teamQuery, Boolean isAdmin);

    /**
     * 修改队伍信息
     *
     * @param teamUpdateRequest
     * @param loginUser
     * @return
     */
    boolean updateTeam(TeamUpdateRequest teamUpdateRequest, User loginUser);

    /**
     * 加入队伍
     *
     * @param teamJoinRequest
     * @param loginUser
     * @return
     */
    Boolean joinTeam(TeamJoinRequest teamJoinRequest, User loginUser);

    /**
     * 退出队伍
     *
     * @param teamQuitRequest
     * @param loginUser
     * @return
     */
    Boolean quitTeam(TeamQuitRequest teamQuitRequest, User loginUser);

    /**
     * 删除（解散）队伍
     *
     * @param teamId
     * @param loginUser
     * @return
     */
    Boolean delTeam(Long teamId, User loginUser);
}
