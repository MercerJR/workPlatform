package com.project.workplatform.service;

import com.project.workplatform.data.WsMessageResponse;
import com.project.workplatform.data.request.announcement.PublishAnnouncementRequest;
import com.project.workplatform.data.request.chatInfo.UpdateChatListRequest;
import com.project.workplatform.exception.CustomException;
import com.project.workplatform.exception.CustomExceptionType;
import com.project.workplatform.exception.ExceptionMessage;
import com.project.workplatform.util.DateFormatUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * @author zengjingran
 */
@Service
public class AnnouncementService {

    @Autowired
    private ChatInfoService chatInfoService;

    public void publish(PublishAnnouncementRequest publishAnnouncementRequest, Integer userId) {
        //校验用户是否进入工作室
        checkInStudio(publishAnnouncementRequest.getStudioId());
        //TODO 构建推文数据结构，放入MySQL，并获取刚刚插入的id
        //TODO 【建表】此处需要新建公告表
        int announcementId = 0;
        //调用公众号发布方法，采用伪WS方式
        publicUserSendMessage(publishAnnouncementRequest, announcementId);
    }

    private void checkInStudio(Integer studioId) {
        if (studioId == null || studioId <= 0) {
            throw new CustomException(CustomExceptionType.NORMAL_ERROR, ExceptionMessage.PLEASE_ENTER_STUDIO);
        }
    }

    public void publicUserSendMessage(PublishAnnouncementRequest publishAnnouncementRequest, int announcementId) {
        //TODO 通过studioId获取公众号用户id
        //TODO 【修改表结构】需要更改工作室表结构，添加工作室公众号id属性；需要在创建工作室方法中，添加创建工作室公众号的功能；
        int chatId = 0;
        Set<Integer> memberSet = publishAnnouncementRequest.getMemberSet();

        //更新用户聊天列表,并更新用户与公众号聊天记录
        UpdateChatListRequest updateChatListRequest = new UpdateChatListRequest(chatId, 0);
        String time = DateFormatUtil.getStringDateByMiles(System.currentTimeMillis(), DateFormatUtil.MINUTE_FORMAT);
        String content = "hello，你收到新的通知推文了，点击下方的链接可以一键查看，也可以到'消息推送'里进行查看\n" + "{" + announcementId + "}";
        for (Integer memberId : memberSet) {
            //更新用户聊天列表
            chatInfoService.updateChatList(updateChatListRequest, memberId);
            //更新用户聊天记录
            WsMessageResponse messageResponse = new WsMessageResponse();
            messageResponse.setSenderId(chatId);
            messageResponse.setTargetId(memberId);
            messageResponse.setContent(content);
            messageResponse.setTime(time);
            chatInfoService.insertPersonalMsgRecord(messageResponse);
        }
    }
}
