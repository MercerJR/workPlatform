package com.project.workplatform.util.wsUtil;

import com.alibaba.fastjson.JSON;
import com.project.workplatform.data.WsMessageResponse;

import javax.websocket.EncodeException;
import javax.websocket.EndpointConfig;

public class WsMsgEncoder implements javax.websocket.Encoder.Text<WsMessageResponse>{

    @Override
    public String encode(WsMessageResponse wsMessageResponse) throws EncodeException {
        return JSON.toJSONString(wsMessageResponse);
    }

    @Override
    public void init(EndpointConfig endpointConfig) {

    }

    @Override
    public void destroy() {

    }
}
