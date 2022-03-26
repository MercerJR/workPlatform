package com.project.workplatform.controller;

import com.project.workplatform.data.WsMessage;
import com.project.workplatform.data.WsMessageResponse;
import com.project.workplatform.data.enums.WsMsgTypeEnum;
import com.project.workplatform.pojo.User;
import com.project.workplatform.pojo.UserInfo;
import com.project.workplatform.service.UserService;
import com.project.workplatform.util.JwtUtil;
import com.project.workplatform.util.wsUtil.WsMsgDecoder;
import com.project.workplatform.util.wsUtil.WsMsgEncoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ServerEndpoint(value = "/chat", decoders = {WsMsgDecoder.class}, encoders = {WsMsgEncoder.class})
@Component
@Slf4j
public class WebSocketController {

    private Session session;

    private static Map<Session, UserInfo> users = new ConcurrentHashMap<>();

    private static UserService userService;

    @Autowired
    public void setUserService(UserService userService) {
        WebSocketController.userService = userService;
    }

    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        log.info("连接成功");
    }

    @OnClose
    public void onClose(Session session) {
        log.info("连接关闭");
    }

    @OnMessage
    public WsMessageResponse onMsg(WsMessage wsMessage) {
        WsMsgTypeEnum typeEnum = WsMsgTypeEnum.valueOfEnum(wsMessage.getType());
        WsMessageResponse messageResponse = new WsMessageResponse();
        switch (typeEnum) {
            case AUTH:
                messageResponse = verifyTokenAndGetUser(wsMessage.getContent());
                break;
            case TEXT_MSG:
                messageResponse = sendMessage(wsMessage);
                break;
            default:
                break;
        }
        return messageResponse;
    }

    private WsMessageResponse verifyTokenAndGetUser(String token) {
        Integer id = JwtUtil.getId(token);
        User user = userService.getUserLogInfo(id);
        boolean flag = JwtUtil.verifyToken(token, user.getPhoneNumber(), user.getPassword());
        WsMessageResponse messageResponse = new WsMessageResponse();
        if (flag){
            UserInfo userInfo = userService.getUserInfo(user.getId());
            users.put(session,userInfo);
            messageResponse.setSenderId(userInfo.getUserId());
            messageResponse.setSenderName(userInfo.getName());
        }
        messageResponse.setCode(flag ? 0 : 2);
        return messageResponse;
    }

    private WsMessageResponse sendMessage(WsMessage wsMessage) {
        WsMessageResponse messageResponse = new WsMessageResponse();
        //如果用户没有登陆，则返回未身份错误
        if (users.get(session) == null){
            messageResponse.setCode(2);
            return messageResponse;
        }
        messageResponse.setContent(wsMessage.getContent());
        return messageResponse;
    }

}
