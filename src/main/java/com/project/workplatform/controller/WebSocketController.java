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
        log.info("????????????");
    }

    @OnClose
    public void onClose(Session session) {
        Integer userId = USER_MAP.get(session).getUserId();
        SESSION_MAP.remove(userId);
        USER_MAP.remove(session);
        log.info("????????????");
    }

    @OnMessage
    public void onMsg(WsMessage wsMessage) {
        WsMsgTypeEnum typeEnum = WsMsgTypeEnum.valueOfEnum(wsMessage.getType());
        switch (typeEnum) {
            case AUTH:
                verifyTokenAndGetUser(wsMessage.getContent());
                //???redis?????????????????????????????????????????????MySQL??????????????????ack_id???????????????????????????
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
        //???????????????????????????????????????????????????
        if (senderInfo == null) {
            messageResponse.setCode(2);
            doSend(session, messageResponse);
            return;
        }
        WsMsgTargetTypeEnum targetTypeEnum = WsMsgTargetTypeEnum.valueOfEnum(wsMessage.getTargetType());
        Integer targetId = wsMessage.getTargetId();
        Integer studioId = wsMessage.getStudioId();
        //??????wsMessageResponse??????
        messageResponse.setSenderId(senderInfo.getUserId());
        messageResponse.setSenderName(senderInfo.getName());
        messageResponse.setContent(wsMessage.getContent());
        messageResponse.setTime(DateFormatUtil.getStringDateByMiles(System.currentTimeMillis(), DateFormatUtil.MINUTE_FORMAT));
        messageResponse.setTargetId(targetId);
        messageResponse.setTargetType(wsMessage.getTargetType());
        switch (targetTypeEnum) {
            //????????????
            case PERSONAL:
                //???wsMessageResponse??????MySQL???personal_msg_record??????
                int personalMsgAckId = chatInfoService.insertPersonalMsgRecord(messageResponse);
                //??????????????????????????????????????????????????????????????????????????????????????????msg_ack_id??????????????????????????????redis???????????????????????????????????????????????????????????????????????????
                doSend(session, messageResponse);
                //???friend???????????????msg_ack_id??????
                friendService.updateMsgAckId(personalMsgAckId, senderInfo.getUserId(), targetId);
                //??????????????????????????????
                chatInfoService.updateChatList(new UpdateChatListRequest(senderInfo.getUserId(), messageResponse.getTargetType()), targetId);
                Session targetSession = SESSION_MAP.get(targetId);
                if (targetSession != null) {
                    doSend(targetSession, messageResponse);
                    //???friend???????????????msg_ack_id??????
                    friendService.updateMsgAckId(personalMsgAckId, targetId, senderInfo.getUserId());
                } else {
                    //redis????????????????????????????????????
                    //?????????????????????????????????????????????????????????
                    String redisKey = Constant.REDIS_NOT_READ_MSG_SENDER_KEY_PREFIX + targetId;
                    redisTemplate.opsForSet().add(redisKey, senderInfo.getUserId());
                }
                break;
            //??????
            case GROUP:
                //???wsMessageResponse??????MySQL???group_msg_record??????????????????msgAckId
                int groupMsgAckId = chatInfoService.insertGroupMsgRecord(messageResponse);
                //??????targetId???MySQL???user_group????????????????????????????????????
                List<MemberResponse> memberList = groupService.getMemberList(senderInfo.getUserId(), targetId);
                //??????????????????????????????????????????session
                for (MemberResponse member : memberList){
                    //??????????????????
                    chatInfoService.updateChatList(new UpdateChatListRequest(targetId, messageResponse.getTargetType()),member.getUserId());
                    //?????????????????????????????????????????????user_group??????msg_ack_id
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
