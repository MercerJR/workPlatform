package com.project.workplatform.service;

import com.project.workplatform.dao.FriendApplyMapper;
import com.project.workplatform.dao.FriendMapper;
import com.project.workplatform.data.request.friend.ApplyFriendRequest;
import com.project.workplatform.data.request.friend.DealApplyRequest;
import com.project.workplatform.data.request.friend.DeleteFriendRequest;
import com.project.workplatform.exception.CustomException;
import com.project.workplatform.exception.CustomExceptionType;
import com.project.workplatform.exception.ExceptionMessage;
import com.project.workplatform.pojo.Friend;
import com.project.workplatform.pojo.FriendApply;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @Author: Mercer JR
 * @Date: 2022/1/9 14:44
 */
@Service
@Transactional(rollbackFor = RuntimeException.class)
public class FriendService {

    @Autowired
    private FriendMapper mapper;

    @Autowired
    private FriendApplyMapper applyMapper;

    public void applyFriend(Integer userId, ApplyFriendRequest applyFriendRequest) {
        //检查是否已经是好友
        if (isAlreadyFriend(userId,applyFriendRequest.getTargetId())){
            throw new CustomException(CustomExceptionType.NORMAL_ERROR, ExceptionMessage.ALREADY_FRIEND);
        }
        FriendApply apply = new FriendApply();
        apply.setUserId(userId);
        apply.setTargetId(applyFriendRequest.getTargetId());
        apply.setApplyMessage(applyFriendRequest.getApplyMessage());
        applyMapper.insertSelective(apply);

        //TODO 用消息队列设置消息通知
    }

    public void dealApply(Integer userId, DealApplyRequest dealApplyRequest) {
        final int agree = 1,disagree = 2;
        int tag = 0;
        FriendApply apply = applyMapper.selectByPrimaryKey(dealApplyRequest.getApplyId());
        //检查请求是否已经被处理过
        if (apply.getTag() != 0){
            throw new CustomException(CustomExceptionType.NORMAL_ERROR,ExceptionMessage.ALREADY_DEAL_APPLY);
        }
        if (dealApplyRequest.isResponse()) {
            //此处是targetId在操作，因此targetId的targetId是apply中的userId
            int targetId = apply.getUserId();
            //检查是否已经是好友
            if (isAlreadyFriend(userId,targetId)){
                throw new CustomException(CustomExceptionType.NORMAL_ERROR, ExceptionMessage.ALREADY_FRIEND);
            }
            Friend friend1 = new Friend(userId, targetId);
            Friend friend2 = new Friend(targetId, userId);
            mapper.insert(friend1);
            mapper.insert(friend2);
            tag = agree;
        } else {
            tag = disagree;
        }
        //更新apply的tag字段
        apply.setTag(tag);
        applyMapper.updateByPrimaryKey(apply);

        //TODO 用消息队列设置消息通知 同意和拒绝的消息内容不一样
    }

    private boolean isAlreadyFriend(int userId,int targetId){
        return mapper.selectByUserAndFriend(userId, targetId) != null && mapper.selectByUserAndFriend(targetId, userId) != null;
    }

    public void delete(Integer userId, DeleteFriendRequest deleteFriendRequest) {
        mapper.deleteByFriend(userId,deleteFriendRequest.getFriendId());
        mapper.deleteByFriend(deleteFriendRequest.getFriendId(),userId);
    }
}
