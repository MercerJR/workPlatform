package com.project.workplatform.service;

import com.project.workplatform.dao.AnnouncementMapper;
import com.project.workplatform.dao.StudioMapper;
import com.project.workplatform.dao.UserInfoMapper;
import com.project.workplatform.data.WsMessageResponse;
import com.project.workplatform.data.enums.WsMsgTargetTypeEnum;
import com.project.workplatform.data.request.announcement.PublishAnnouncementRequest;
import com.project.workplatform.data.request.chatInfo.UpdateChatListRequest;
import com.project.workplatform.data.response.AnnouncementResponse;
import com.project.workplatform.exception.CustomException;
import com.project.workplatform.exception.CustomExceptionType;
import com.project.workplatform.exception.ExceptionMessage;
import com.project.workplatform.pojo.Announcement;
import com.project.workplatform.util.DateFormatUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * @author zengjingran
 */
@Service
public class AnnouncementService {

    @Autowired
    private ChatInfoService chatInfoService;

    @Autowired
    private AnnouncementMapper mapper;

    @Autowired
    private StudioMapper studioMapper;

    @Autowired
    private UserInfoMapper userInfoMapper;

    public void publish(PublishAnnouncementRequest publishAnnouncementRequest, Integer userId) {
        //校验用户是否进入工作室
        checkInStudio(publishAnnouncementRequest.getStudioId());
        //构建推文数据结构，放入MySQL，并获取刚刚插入的id
        Announcement announcement = new Announcement();
        announcement.setTitle(publishAnnouncementRequest.getTitle());
        announcement.setContent(publishAnnouncementRequest.getContent());
        announcement.setPublisherId(userId);
        announcement.setStudioId(publishAnnouncementRequest.getStudioId());
        StringBuilder builder = new StringBuilder();
        Set<Integer> memberSet = publishAnnouncementRequest.getMemberSet();
        Iterator<Integer> iterator = memberSet.iterator();
        while (iterator.hasNext()){
            builder.append(iterator.next());
            if (iterator.hasNext()){
                builder.append(",");
            }
        }
        announcement.setReaderId(builder.toString());
        //此处需要新建公告表
        mapper.insertSelective(announcement);
        int announcementId = mapper.getLastId();
        //调用公众号发布方法，采用伪WS方式
        publicUserSendMessage(publishAnnouncementRequest, announcementId);
    }

    private void checkInStudio(Integer studioId) {
        if (studioId == null || studioId <= 0) {
            throw new CustomException(CustomExceptionType.NORMAL_ERROR, ExceptionMessage.PLEASE_ENTER_STUDIO);
        }
    }

    public void publicUserSendMessage(PublishAnnouncementRequest publishAnnouncementRequest, int announcementId) {
        //通过studioId获取公众号用户id
        //【修改表结构】需要更改工作室表结构，添加工作室公众号id属性；需要在创建工作室方法中，添加创建工作室公众号的功能；
        int chatId = studioMapper.selectByPrimaryKey(publishAnnouncementRequest.getStudioId()).getHelperId();
        Set<Integer> memberSet = publishAnnouncementRequest.getMemberSet();

        //更新用户聊天列表,并更新用户与公众号聊天记录
        UpdateChatListRequest updateChatListRequest = new UpdateChatListRequest(chatId, 2);
        String time = DateFormatUtil.getStringDateByMiles(System.currentTimeMillis(), DateFormatUtil.MINUTE_FORMAT);
        String content = "hello，你收到新的通知推文了，点击下方的链接可以一键查看，也可以到'消息推送'里进行查看#" + announcementId;
        for (Integer memberId : memberSet) {
            if (memberId < 0){
                continue;
            }
            //更新用户聊天列表
            chatInfoService.updateChatList(updateChatListRequest, memberId);
            //更新用户聊天记录
            WsMessageResponse messageResponse = new WsMessageResponse();
            messageResponse.setSenderId(chatId);
            messageResponse.setTargetId(memberId);
            messageResponse.setContent(content);
            messageResponse.setTime(time);
            messageResponse.setTargetType(WsMsgTargetTypeEnum.PUBLIC_USER.getType());
            chatInfoService.insertPersonalMsgRecord(messageResponse);
        }
    }

    public List<AnnouncementResponse> getAnnouncementList(Integer studioId) {
        checkInStudio(studioId);
        List<Announcement> list = mapper.selectByStudio(studioId);
        List<AnnouncementResponse> responses = new ArrayList<>();
        for (Announcement announcement : list){
            AnnouncementResponse announcementResponse = new AnnouncementResponse();
            announcementResponse.setTitle(announcement.getTitle());
            announcementResponse.setContent(announcement.getContent());
            announcementResponse.setAnnouncementId(announcement.getId());
            announcementResponse.setPublisherId(announcement.getPublisherId());
            announcementResponse.setPublisherName(userInfoMapper.selectByUser(announcement.getPublisherId()).getName());
            announcementResponse.setTime(DateFormatUtil.getStringDateByDate(announcement.getTime(),DateFormatUtil.MINUTE_FORMAT));
            announcementResponse.setSeq(responses.size());
            responses.add(announcementResponse);
        }
        return responses;
    }

    public AnnouncementResponse getOneAnnouncement(Integer announcementId) {
        Announcement announcement = mapper.selectByPrimaryKey(announcementId);
        AnnouncementResponse announcementResponse = new AnnouncementResponse();
        announcementResponse.setTitle(announcement.getTitle());
        announcementResponse.setContent(announcement.getContent());
        announcementResponse.setAnnouncementId(announcement.getId());
        announcementResponse.setPublisherId(announcement.getPublisherId());
        announcementResponse.setPublisherName(userInfoMapper.selectByUser(announcement.getPublisherId()).getName());
        announcementResponse.setTime(DateFormatUtil.getStringDateByDate(announcement.getTime(),DateFormatUtil.MINUTE_FORMAT));
        return announcementResponse;
    }
}
