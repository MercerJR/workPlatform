package com.project.workplatform.dao;

import com.project.workplatform.pojo.PersonalMsgRecord;

/**
 * @Author: Mercer JR
 * @Date: 2022/4/1 9:06
 */
public interface PersonalMsgRecordMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(PersonalMsgRecord record);

    int insertSelective(PersonalMsgRecord record);

    PersonalMsgRecord selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(PersonalMsgRecord record);

    int updateByPrimaryKey(PersonalMsgRecord record);
}