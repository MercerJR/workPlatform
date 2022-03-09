package com.project.workplatform.dao;

import com.project.workplatform.pojo.UserInfo;

/**
 * @Author: Mercer JR
 * @Date: 2022/1/13 10:31
 */
public interface UserInfoMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(UserInfo record);

    int insertSelective(UserInfo record);

    UserInfo selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(UserInfo record);

    int updateByPrimaryKey(UserInfo record);

    UserInfo selectByUser(int userId);

    Integer selectUserIdByPhoneNumber(String phoneNumber);

    Integer selectUserIdByName(String name);
}