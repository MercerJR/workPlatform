package com.project.workplatform.controller;

import com.project.workplatform.data.Constant;
import com.project.workplatform.data.WsMessage;
import com.project.workplatform.data.WsMessageResponse;
import com.project.workplatform.data.enums.WsMsgTargetTypeEnum;
import com.project.workplatform.data.enums.WsMsgTypeEnum;
import com.project.workplatform.pojo.User;
import com.project.workplatform.pojo.UserInfo;
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
                //TODO 从redis中拉取未读消息的消息来源，并从MySQL中根据来源和ack_id拉取最新的消息记录
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
        WsMessageResponse messageResponse = new WsMessageResponse();
        if (flag) {
            UserInfo userInfo = userService.getUserInfo(user.getId());
            USER_MAP.put(session, userInfo);
            SESSION_MAP.put(userInfo.getUserId(), session);
            messageResponse.setSenderId(userInfo.getUserId());
            messageResponse.setSenderName(userInfo.getName());
        }
        messageResponse.setCode(flag ? 0 : 2);
        doSend(session, messageResponse);
    }

    private void sendMessage(WsMessage wsMessage) {
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
        switch (targetTypeEnum) {
            //单人聊天
            case PERSONAL:
                Integer studioId = wsMessage.getStudioId();
                //构建WsMessageResponse对象
                messageResponse.setSenderId(senderInfo.getUserId());
                messageResponse.setSenderName(studioId == null || studioId <= 0 ? senderInfo.getName() :
                        studioService.getUserStudioInfo(senderInfo.getUserId(), studioId).getInsideAlias());
                messageResponse.setContent(wsMessage.getContent());
                messageResponse.setTime(DateFormatUtil.getStringDateByMiles(System.currentTimeMillis(), DateFormatUtil.MINUTE_FORMAT));
                //TODO 将wsMessageResponse写进MySQL的personal_msg_record表中
                //分为对方在线和不在线两种情况：如果对方在线，则实时发送并更新msg_ack_id；如果对方不在线，在redis中记录哪个用户发来了消息，上线后会自动拉取最新消息
                Session targetSession = SESSION_MAP.get(targetId);
                if (targetSession != null) {
                    doSend(session, messageResponse);
                    doSend(targetSession, messageResponse);
                    //TODO 将friend表中的msg_ack_id更新
                } else {
                    //redis中记录未收到的信息的来源
                    String redisKey = Constant.REDIS_NOT_READ_MSG_SENDER_KEY_PREFIX + targetId;
                    redisTemplate.opsForSet().add(redisKey,senderInfo.getUserId());
                }
                break;
            //群聊
            case GROUP:
                //TODO 将wsMessageResponse写进MySQL的group_msg_record表中
                //TODO 根据targetId从MySQL的user_group表中获取该群聊的用户列表
                //TODO 遍历用户列表，分别找出在线和离线的用户的session列表
                //TODO 给在线的用户实时推送消息，并且更新user_group中的msg_ack_id
                //TODO 离线的用户不用操作
                break;
            default:
                break;
        }
    }

    private void doSend(Session session, WsMessageResponse messageResponse) {
        try {
            session.getBasicRemote().sendObject(messageResponse);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (EncodeException e) {
            e.printStackTrace();
        }
    }

}
