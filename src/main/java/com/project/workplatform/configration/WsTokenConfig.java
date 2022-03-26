package com.project.workplatform.configration;

import com.project.workplatform.data.Constant;
import com.project.workplatform.exception.CustomException;
import com.project.workplatform.exception.CustomExceptionType;
import com.project.workplatform.exception.ExceptionMessage;

import javax.websocket.HandshakeResponse;
import javax.websocket.server.HandshakeRequest;
import javax.websocket.server.ServerEndpointConfig;
import java.util.List;
import java.util.Map;

public class WsTokenConfig extends ServerEndpointConfig.Configurator {

    @Override
    public void modifyHandshake(ServerEndpointConfig sec, HandshakeRequest request, HandshakeResponse response) {
        Map<String, List<String>> headers = request.getHeaders();
        List<String> list = headers.get(Constant.JWT_TOKEN);
        if (list == null || list.isEmpty()){
            throw new CustomException(CustomExceptionType.NORMAL_ERROR, ExceptionMessage.TOKEN_NOT_EXIST);
        }
        String token = list.get(0);
        sec.getUserProperties().put(Constant.JWT_TOKEN, token);
    }

}
