package com.project.workplatform.util.wsUtil;

import com.alibaba.fastjson.JSON;
import com.project.workplatform.data.WsMessage;

import javax.websocket.DecodeException;
import javax.websocket.EndpointConfig;

public class WsMsgDecoder implements javax.websocket.Decoder.Text<WsMessage>  {
    @Override
    public WsMessage decode(String s) throws DecodeException {
        return JSON.parseObject(s, WsMessage.class);
    }

    @Override
    public boolean willDecode(String s) {
        return true;
    }

    @Override
    public void init(EndpointConfig endpointConfig) {

    }

    @Override
    public void destroy() {

    }
}
