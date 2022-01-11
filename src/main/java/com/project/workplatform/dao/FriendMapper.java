package com.project.workplatform.dao;

import com.project.workplatform.pojo.Friend;
import org.apache.ibatis.annotations.Param;

/**
 * @Author: Mercer JR
 * @Date: 2022/1/9 14:45
 */
public interface FriendMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Friend record);

    int insertSelective(Friend record);

    Friend selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Friend record);

    int updateByPrimaryKey(Friend record);

    Friend selectByUserAndFriend(@Param("userId") int userId,@Param("friendId") int friendId);
}