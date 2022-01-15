package com.project.workplatform.dao;

import com.project.workplatform.pojo.GroupApply;

/**
 * @Author: Mercer JR
 * @Date: 2022/1/15 18:18
 */
public interface GroupApplyMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(GroupApply record);

    int insertSelective(GroupApply record);

    GroupApply selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(GroupApply record);

    int updateByPrimaryKey(GroupApply record);
}