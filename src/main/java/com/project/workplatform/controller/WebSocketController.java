package com.project.workplatform.controller;

import com.project.workplatform.data.Constant;
import com.project.workplatform.data.WsMessage;
import com.project.workplatform.data.WsMessageResponse;
import com.project.workplatform.data.enums.WsMsgTargetTypeEnum;
import com.project.workplatform.data.enums.WsMsgTypeEnum;
import com.project.workplatform.data.request.chatInfo.UpdateChatListRequest;
import com.project.workplatform.data.response.group.MemberResponse;
import com.project.workplatform.pojo.User;
import com.project.workplatform.pojo.UserInfo;
import com.project.workplatform.service.ChatInfoService;
import com.project.workplatform.service.FriendService;
import com.project.workplatform.service.GroupService;
import com.project.workplatform.service.StudioService;
import com.project.workplatform.service.UserService;
import com.project.workplatform.util.DateFormatUtil;
import com.project.workplatform.util.JwtUtil;
import com.project.workplatform.util.wsUtil.WsMsgDecoder;
import com.project.workplatform.util.wsUtil.WsMsgEncoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.websocket.EncodeException;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ServerEndpoint(value = "/chat", decoders = {WsMsgDecoder.class}, encoders = {WsMsgEncoder.class})
@Component
@Slf4j
public class WebSocketController {

    private Session session;

    private static final Map<Session, UserInfo> USER_MAP = new ConcurrentHashMap<>();

    private static final Map<Integer, Session> SESSION_MAP = new ConcurrentHashMap<>();

    private static UserService userService;

    @Autowired
    public void setUserService(UserService userService) {
        WebSocketController.userService = userService;
    }

    private static StudioService studioService;

    @Autowired
    private void setStudioService(StudioService studioService) {
        WebSocketController.studioService = studioService;
    }

    private static ChatInfoService chatInfoService;

    @Autowired
    private void setChatInfoService(ChatInfoService chatInfoService) {
        WebSocketController.chatInfoService = chatInfoService;
    }

    private static FriendService friendService;

    @Autowired
    private void setFriendService(FriendService friendService) {
        WebSocketController.friendService = friendService;
    }

    private static GroupService groupService;

    @Autowired
    private void setGroupService(GroupService groupService) {
        WebSocketController.groupService = groupService;
    }

    private static RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private void setRedisTemplate(RedisTemplate<String, Object> redisTemplate) {
        WebSocketController.redisTemplate = redisTemplate;
    }

    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        log.info("连接成功");
    }

    @OnClose
    public void onClose(Session session) {
        Integer userId = USER_MAP.get(session).getUserId();
        SESSION_MAP.remove(userId);
        USER_MAP.remove(session);
        log.info("连接关闭");
    }

    @OnMessage
    public void onMsg(WsMessage wsMessage) {
        WsMsgTypeEnum typeEnum = WsMsgTypeEnum.valueOfEnum(wsMessage.getType());
        switch (typeEnum) {
            case AUTH:
                verifyTokenAndGetUser(wsMessage.getContent());
                //从redis中拉取未读消息的消息来源，并从MySQL中根据来源和ack_id拉取最新的消息记录
                break;
            case TEXT_MSG:
                sendMessage(wsMessage);
                break;
            default:
                break;
        }
    }

    private void verifyTokenAndGetUser(String token) {
        Integer id = JwtUtil.getId(token);
        User user = userService.getUserLogInfo(id);
        boolean flag = JwtUtil.verifyToken(token, user.getPhoneNumber(), user.getPassword());
        if (flag) {
            UserInfo userInfo = userService.getUserInfo(user.getId());
            USER_MAP.put(session, userInfo);
            SESSION_MAP.put(userInfo.getUserId(), session);
        } else {
            WsMessageResponse messageResponse = new WsMessageResponse();
            messageResponse.setCode(2);
            doSend(session, messageResponse);
        }
    }

    private void sendMessage(WsMessage wsMessage) {
        log.info(wsMessage.toString());
        WsMessageResponse messageResponse = new WsMessageResponse();
        UserInfo senderInfo = USER_MAP.get(session);
        //如果用户没有登陆，则返回未身份错误
        if (senderInfo == null) {
            messageResponse.setCode(2);
            doSend(session, messageResponse);
            return;
        }
        WsMsgTargetTypeEnum targetTypeEnum = WsMsgTargetTypeEnum.valueOfEnum(wsMessage.getTargetType());
        Integer targetId = wsMessage.getTargetId();
        Integer studioId = wsMessage.getStudioId();
        //构造wsMessageResponse对象
        messageResponse.setSenderId(senderInfo.getUserId());
        messageResponse.setSenderName(senderInfo.getName());
        messageResponse.setContent(wsMessage.getContent());
        messageResponse.setTime(DateFormatUtil.getStringDateByMiles(System.currentTimeMillis(), DateFormatUtil.MINUTE_FORMAT));
        messageResponse.setTargetId(targetId);
        messageResponse.setTargetType(wsMessage.getTargetType());
        switch (targetTypeEnum) {
            //单人聊天
            case PERSONAL:
                //将wsMessageResponse写进MySQL的personal_msg_record表中
                int personalMsgAckId = chatInfoService.insertPersonalMsgRecord(messageResponse);
                //分为对方在线和不在线两种情况：如果对方在线，则实时发送并更新msg_ack_id；如果对方不在线，在redis中记录哪个用户发来了消息，上线后会自动拉取最新消息
                doSend(session, messageResponse);
                //将friend表中自己的msg_ack_id更新
                friendService.updateMsgAckId(personalMsgAckId, senderInfo.getUserId(), targetId);
                //将对方的聊天列表更新
                chatInfoService.updateChatList(new UpdateChatListRequest(senderInfo.getUserId(), messageResponse.getTargetType()), targetId);
                Session targetSession = SESSION_MAP.get(targetId);
                if (targetSession != null) {
                    doSend(targetSession, messageResponse);
                    //将friend表中对方的msg_ack_id更新
                    friendService.updateMsgAckId(personalMsgAckId, targetId, senderInfo.getUserId());
                } else {
                    //redis中记录未收到的信息的来源
                    //用于记录未读消息记录，暂时没用该段代码
                    String redisKey = Constant.REDIS_NOT_READ_MSG_SENDER_KEY_PREFIX + targetId;
                    redisTemplate.opsForSet().add(redisKey, senderInfo.getUserId());
                }
                break;
            //群聊
            case GROUP:
                //将wsMessageResponse写进MySQL的group_msg_record表中，并获取msgAckId
                int groupMsgAckId = chatInfoService.insertGroupMsgRecord(messageResponse);
                //根据targetId从MySQL的user_group表中获取该群聊的用户列表
                List<MemberResponse> memberList = groupService.getMemberList(senderInfo.getUserId(), targetId);
                //遍历用户列表，找出在线用户的session
                for (MemberResponse member : memberList){
                    //更新聊天列表
                    chatInfoService.updateChatList(new UpdateChatListRequest(targetId, messageResponse.getTargetType()),member.getUserId());
                    //给在线的用户实时推送消息，更新user_group中的msg_ack_id
                    Session memberSession = SESSION_MAP.get(member.getUserId());
                    if (memberSession != null){
                        doSend(memberSession, messageResponse);
                        groupService.updateMsgAckId(groupMsgAckId,member.getUserId(),targetId);
                    }
                }
                break;
            default:
                break;
        }
    }

    private void doSend(Session session, WsMessageResponse messageResponse) {
        try {
            session.getBasicRemote().sendObject(messageResponse);
        } catch (IOException | EncodeException e) {
            e.printStackTrace();
        }
    }

}
