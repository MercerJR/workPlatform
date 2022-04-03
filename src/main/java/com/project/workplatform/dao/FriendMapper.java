package com.project.workplatform.dao;

import com.project.workplatform.data.response.friend.FriendInfoResponse;import com.project.workplatform.data.response.friend.FriendListResponse;import com.project.workplatform.pojo.Friend;import org.apache.ibatis.annotations.Param;import java.util.List;

/**
 * @Author: Mercer JR
 * @Date: 2022/4/1 9:16
 */
public interface FriendMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Friend record);

    int insertSelective(Friend record);

    Friend selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Friend record);

    int updateByPrimaryKey(Friend record);

    Friend selectByUserAndFriend(@Param("userId") int userId, @Param("friendId") int friendId);

    void deleteByFriend(@Param("userId") int userId, @Param("friendId") int friendId);

    List<FriendListResponse> selectFriendList(Integer userId);

    FriendInfoResponse selectFriendInfo(int friendId);

    int updateMsgAckIdByUserAndFriend(@Param("msgAckId") int msgAckId, @Param("userId") int userId, @Param("friendId") int friendId);
}