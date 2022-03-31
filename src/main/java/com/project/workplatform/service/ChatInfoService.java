package com.project.workplatform.service;

import com.alibaba.fastjson.JSON;
import com.project.workplatform.dao.UserInfoMapper;
import com.project.workplatform.dao.UserStudioMapper;
import com.project.workplatform.data.Constant;
import com.project.workplatform.data.WsMessageResponse;
import com.project.workplatform.data.request.chatInfo.UpdateChatListRequest;
import com.project.workplatform.data.response.chatInfo.ChatListResponse;
import com.project.workplatform.pojo.UserInfo;
import com.project.workplatform.pojo.UserStudio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ChatInfoService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private UserStudioMapper userStudioMapper;

    @Autowired
    private UserInfoMapper userInfoMapper;

    public void updateChatList(UpdateChatListRequest updateChatListRequest, Integer userId) {
        String redisKey = Constant.REDIS_CHAT_LIST_KEY_PREFIX + userId;
        List<Object> chatList = redisTemplate.opsForList().range(redisKey, 0, -1);
        //遍历list，如果已经存在该聊天记录，则先把它删除后再leftPush
        if (chatList != null && !chatList.isEmpty()){
            for (Object obj : chatList) {
                String str = (String) obj;
                UpdateChatListRequest item = JSON.parseObject(str, UpdateChatListRequest.class);
                if (item.getChatId().equals(updateChatListRequest.getChatId())) {
                    redisTemplate.opsForList().remove(redisKey, 1, str);
                }
            }
        }
        String jsonStr = JSON.toJSONString(updateChatListRequest);
        redisTemplate.opsForList().leftPush(redisKey,jsonStr);
    }

    public List<ChatListResponse> getChatList(int studioId,Integer userId) {
        String redisKey = Constant.REDIS_CHAT_LIST_KEY_PREFIX + userId;
        List<Object> chatList = redisTemplate.opsForList().range(redisKey, 0, -1);
        List<ChatListResponse> responses = new ArrayList<>();
        if (chatList != null && !chatList.isEmpty()){
            for (Object obj : chatList) {
                String str = (String) obj;
                UpdateChatListRequest item = JSON.parseObject(str, UpdateChatListRequest.class);
                ChatListResponse record = new ChatListResponse();
                record.setChatId(item.getChatId());
                record.setTargetType(item.getTargetType());
                if (item.getTargetType() == 0) {
                    UserStudio userStudio = userStudioMapper.selectByUser(item.getChatId());
                    UserInfo userInfo = userInfoMapper.selectByUser(item.getChatId());
                    //对内部用户和外部用户区分赋值
                    if (userStudio != null && userStudio.getStudioId() == studioId) {
                        record.setInsideType(1);
                        record.setInsideTag("内部用户");
                        record.setChatName(userStudio.getInsideAlias() == null ? userInfo.getName() : userStudio.getInsideAlias());
                    } else {
                        record.setInsideType(0);
                        record.setInsideTag("外部用户");
                        record.setChatName(userInfo.getName());
                    }
                    record.setIcon("");
                } else {
                    //TODO 为群聊chat列表赋值
                }
                responses.add(record);
            }
        }
        return responses;
    }

    public int insertPersonalMsgRecord(WsMessageResponse messageResponse) {
        //TODO 引入personal_msg_record表，执行表的insert方法，并获取插入的主键自增id（获取自增id的方法为"SELECT LAST_INSERT_ID()"）
        return 0;
    }
}
