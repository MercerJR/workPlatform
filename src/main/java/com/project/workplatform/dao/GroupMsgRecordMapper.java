package com.project.workplatform.dao;

import com.project.workplatform.pojo.GroupMsgRecord;

/**
 * @Author: Mercer JR
 * @Date: 2022/4/1 9:07
 */
public interface GroupMsgRecordMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(GroupMsgRecord record);

    int insertSelective(GroupMsgRecord record);

    GroupMsgRecord selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(GroupMsgRecord record);

    int updateByPrimaryKey(GroupMsgRecord record);
}