package com.project.workplatform.dao;

import com.project.workplatform.pojo.FriendApply;

import java.util.List;

/**
 * @Author: Mercer JR
 * @Date: 2022/4/3 15:27
 */
public interface FriendApplyMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(FriendApply record);

    int insertSelective(FriendApply record);

    FriendApply selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(FriendApply record);

    int updateByPrimaryKey(FriendApply record);

    List<FriendApply> selectByUser(Integer userId);
}