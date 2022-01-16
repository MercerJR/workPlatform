package com.project.workplatform.dao;

import com.project.workplatform.data.response.group.MemberResponse;
import com.project.workplatform.pojo.UserGroup;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author: Mercer JR
 * @Date: 2022/1/15 17:25
 */
public interface UserGroupMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(UserGroup record);

    int insertSelective(UserGroup record);

    UserGroup selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(UserGroup record);

    int updateByPrimaryKey(UserGroup record);

    UserGroup selectByUserAndGroup(@Param("userId") Integer userId, @Param("groupId") int groupId);

    List<MemberResponse> selectMemberByGroup(int groupId);
}