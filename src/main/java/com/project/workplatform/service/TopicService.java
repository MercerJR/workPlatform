package com.project.workplatform.service;

import com.project.workplatform.dao.TopicMapper;
import com.project.workplatform.data.request.topic.PublishTopicRequest;
import com.project.workplatform.data.response.Topic.TopicResponse;
import com.project.workplatform.pojo.Topic;
import com.project.workplatform.util.DateFormatUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @Author: Mercer JR
 * @Date: 2022/1/18 10:29
 */
@Service
@Transactional(rollbackFor = RuntimeException.class)
public class TopicService {

    @Autowired
    private TopicMapper mapper;

    public void publicTopic(Integer userId, PublishTopicRequest publishTopicRequest) {
        Topic topic = new Topic();
        topic.setPublisherId(userId);
        topic.setTitle(publishTopicRequest.getTitle());
        topic.setContent(publishTopicRequest.getContent());
        topic.setImg(convertListToString(publishTopicRequest.getImgList()));
        topic.setLabel(convertListToString(publishTopicRequest.getLabelList()));
        topic.setStudioId(publishTopicRequest.getStudioId());
        mapper.insertSelective(topic);
    }

    public List<TopicResponse> getTopicList(Integer userId,int studioId) {
        //TODO 校验该用户是否为该工作室的成员

        List<Topic> list = mapper.selectByStudio(studioId);
        if(CollectionUtils.isEmpty(list)){
            return null;
        }
        List<TopicResponse> topicList = new ArrayList<>();
        for (Topic topic : list){
            TopicResponse record = new TopicResponse();
            record.setTopicId(topic.getId());
            record.setPublisherId(topic.getPublisherId());
            record.setTitle(topic.getTitle());
            record.setContent(topic.getContent());
            record.setImgList(convertStringToList(topic.getImg()));
            record.setLabelList(convertStringToList(topic.getLabel()));
            record.setLikeNum(topic.getLikeNum());
            record.setCommentNum(topic.getCommentNum());
            record.setCreateTime(DateFormatUtil.getStringDateByDate(
                    topic.getCreateTime(),DateFormatUtil.MINUTE_FORMAT));
            topicList.add(record);
        }
        return topicList;
    }

    private String convertListToString(List<String> list){
        if (CollectionUtils.isEmpty(list)){
            return null;
        }
        StringBuilder imgBuilder = new StringBuilder();
        for (int i = 0;i < list.size();i++){
            imgBuilder.append(list.get(i));
            if (i != list.size() - 1){
                imgBuilder.append(",");
            }
        }
        return imgBuilder.toString();
    }

    private List<String> convertStringToList(String str){
        if (!StringUtils.hasLength(str)){
            return null;
        }
        String[] split = str.split(",");
        return Arrays.asList(split);
    }

}