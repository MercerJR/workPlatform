package com.project.workplatform.service;

import com.alibaba.fastjson.JSON;
import com.project.workplatform.dao.GroupMapper;
import com.project.workplatform.dao.GroupMsgRecordMapper;
import com.project.workplatform.dao.PersonalMsgRecordMapper;
import com.project.workplatform.dao.StudioMapper;
import com.project.workplatform.dao.UserInfoMapper;
import com.project.workplatform.dao.UserStudioMapper;
import com.project.workplatform.data.Constant;
import com.project.workplatform.data.WsMessageResponse;
import com.project.workplatform.data.enums.WsMsgTargetTypeEnum;
import com.project.workplatform.data.request.chatInfo.UpdateChatListRequest;
import com.project.workplatform.data.response.chatInfo.ChatListResponse;
import com.project.workplatform.pojo.Group;
import com.project.workplatform.pojo.GroupMsgRecord;
import com.project.workplatform.pojo.PersonalMsgRecord;
import com.project.workplatform.pojo.UserInfo;
import com.project.workplatform.pojo.UserStudio;
import org.springframework.beans.BeanUtils;
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

    @Autowired
    private PersonalMsgRecordMapper personalMsgRecordMapper;

    @Autowired
    private GroupMsgRecordMapper groupMsgRecordMapper;

    @Autowired
    private GroupMapper groupMapper;

    @Autowired
    private StudioMapper studioMapper;

    public void updateChatList(UpdateChatListRequest updateChatListRequest, Integer userId) {
        String redisKey = Constant.REDIS_CHAT_LIST_KEY_PREFIX + userId;
        List<Object> chatList = redisTemplate.opsForList().range(redisKey, 0, -1);
        //遍历list，如果已经存在该聊天记录，则先把它删除后再leftPush
        if (chatList != null && !chatList.isEmpty()) {
            for (Object obj : chatList) {
                String str = (String) obj;
                UpdateChatListRequest item = JSON.parseObject(str, UpdateChatListRequest.class);
                if (item.getChatId().equals(updateChatListRequest.getChatId()) && item.getTargetType().equals(updateChatListRequest.getTargetType())) {
                    redisTemplate.opsForList().remove(redisKey, 1, str);
                    break;
                }
            }
        }
        String jsonStr = JSON.toJSONString(updateChatListRequest);
        redisTemplate.opsForList().leftPush(redisKey, jsonStr);
    }

    public List<ChatListResponse> getChatList(Integer studioId, Integer userId) {
        String redisKey = Constant.REDIS_CHAT_LIST_KEY_PREFIX + userId;
        List<Object> chatList = redisTemplate.opsForList().range(redisKey, 0, -1);
        List<ChatListResponse> responses = new ArrayList<>();
        if (chatList != null && !chatList.isEmpty()) {
            for (Object obj : chatList) {
                String str = (String) obj;
                UpdateChatListRequest item = JSON.parseObject(str, UpdateChatListRequest.class);
                ChatListResponse record = new ChatListResponse();
                record.setChatId(item.getChatId());
                record.setTargetType(item.getTargetType());
                if (item.getTargetType() == 0 || item.getTargetType() == 2) {
                    UserStudio userStudio = userStudioMapper.selectByUserAndStudio(item.getChatId(),studioId);
                    UserInfo userInfo = userInfoMapper.selectByUser(item.getChatId());
                    //对内部用户和外部用户区分赋值
                    if (userStudio != null && userStudio.getStudioId().equals(studioId)) {
                        record.setInsideType(1);
                        record.setInsideTag("内部用户");
                    } else {
                        Integer helperId = studioMapper.selectByPrimaryKey(studioId).getHelperId();
                        if (helperId != null && helperId.equals(item.getChatId())){
                            record.setInsideType(1);
                            record.setInsideTag("公众号");
                        }else {
                            record.setInsideType(0);
                            record.setInsideTag("外部用户");
                        }
                    }
                    record.setChatName(userInfo.getName());
                    record.setIcon("");
                    //获取对应的聊天记录
                } else {
                    //为群聊的record赋值
                    Group group = groupMapper.selectByPrimaryKey(item.getChatId());
                    //对内部群聊和外部群聊区分赋值
                    if (group.getType() == 1 && studioId.equals(group.getStudioId())){
                        record.setInsideType(1);
                        record.setInsideTag("内部群聊");
                    }else {
                        record.setInsideType(0);
                        record.setInsideTag("外部群聊");
                    }
                    record.setChatName(group.getGroupName());
                    record.setIcon("");
                }
                responses.add(record);
            }
        }
        return responses;
    }

    public int insertPersonalMsgRecord(WsMessageResponse messageResponse) {
        PersonalMsgRecord msgRecord = new PersonalMsgRecord();
        msgRecord.setSenderId(messageResponse.getSenderId());
        msgRecord.setTargetId(messageResponse.getTargetId());
        msgRecord.setTime(messageResponse.getTime());
        msgRecord.setContent(messageResponse.getContent());
        personalMsgRecordMapper.insert(msgRecord);
        return personalMsgRecordMapper.getLastInsertId();
    }

    public int insertGroupMsgRecord(WsMessageResponse messageResponse) {
        //将群聊聊天记录存入mysql，并返回msgAckId
        GroupMsgRecord msgRecord = new GroupMsgRecord();
        msgRecord.setSenderId(messageResponse.getSenderId());
        msgRecord.setGroupId(messageResponse.getTargetId());
        msgRecord.setTime(messageResponse.getTime());
        msgRecord.setContent(messageResponse.getContent());
        groupMsgRecordMapper.insert(msgRecord);
        return groupMsgRecordMapper.getLastInsertId();
    }

    public List<WsMessageResponse> getMsgRecord(Integer userId, Integer targetId, Integer targetType, Integer studioId) {
        WsMsgTargetTypeEnum targetTypeEnum = WsMsgTargetTypeEnum.valueOfEnum(targetType);
        List<WsMessageResponse> messageList = new ArrayList<>();
        switch (targetTypeEnum) {
            case PERSONAL:
                List<PersonalMsgRecord> personalMsgRecordList = personalMsgRecordMapper.selectByUserAndFriend(userId, targetId);
                for (PersonalMsgRecord msgRecord : personalMsgRecordList) {
                    UserInfo senderInfo = userInfoMapper.selectByUser(msgRecord.getSenderId());
                    WsMessageResponse message = new WsMessageResponse();
                    BeanUtils.copyProperties(msgRecord, message);
                    message.setCode(0);
                    message.setSenderName(senderInfo.getName());
                    message.setTargetType(targetType);
                    messageList.add(message);
                }
                break;
            case GROUP:
                //获取群聊聊天记录
                List<GroupMsgRecord> groupMsgRecordList = groupMsgRecordMapper.selectByGroup(targetId);
                //构造WsMessageResponse对象，并添加到messageList中
                for (GroupMsgRecord msgRecord : groupMsgRecordList){
                    UserInfo senderInfo = userInfoMapper.selectByUser(msgRecord.getSenderId());
                    Group group = groupMapper.selectByPrimaryKey(targetId);
                    WsMessageResponse message = new WsMessageResponse();
                    message.setCode(0);
                    message.setSenderId(msgRecord.getSenderId());
                    message.setSenderName(senderInfo.getName());
                    message.setTargetId(targetId);
                    message.setTargetType(targetType);
                    message.setTime(msgRecord.getTime());
                    message.setContent(msgRecord.getContent());
                    messageList.add(message);
                }
                break;
            case PUBLIC_USER:
                List<PersonalMsgRecord> publicUserRecordList = personalMsgRecordMapper.selectByUserAndFriend(userId, targetId);
                for (PersonalMsgRecord msgRecord : publicUserRecordList) {
                    UserInfo senderInfo = userInfoMapper.selectByUser(msgRecord.getSenderId());
                    WsMessageResponse message = new WsMessageResponse();
                    BeanUtils.copyProperties(msgRecord, message);
                    message.setCode(0);
                    message.setSenderName(senderInfo.getName());
                    message.setTargetType(targetType);
                    messageList.add(message);
                }
            default:
                break;
        }
        return messageList;
    }
}
