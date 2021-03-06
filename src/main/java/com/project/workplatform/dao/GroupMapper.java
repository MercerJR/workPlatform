package com.project.workplatform.dao;

import com.project.workplatform.data.response.search.SearchGroupResponse;
import com.project.workplatform.pojo.Group;

import java.util.List;

/**
 * @Author: Mercer JR
 * @Date: 2022/1/15 15:57
 */
public interface GroupMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Group record);

    int insertSelective(Group record);

    Group selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Group record);

    int updateByPrimaryKey(Group record);

    int selectCreatorByGroup(int groupId);

    List<SearchGroupResponse> selectGroupInfoByGroupNameFuzzy(String fuzzyGroupName);
}