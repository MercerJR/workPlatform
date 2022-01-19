package com.project.workplatform.service;

import com.project.workplatform.dao.TopicCommentMapper;
import com.project.workplatform.dao.TopicMapper;
import com.project.workplatform.data.Constant;
import com.project.workplatform.data.request.topic.PublishCommentRequest;
import com.project.workplatform.data.request.topic.PublishTopicRequest;
import com.project.workplatform.data.response.Topic.CommentResponse;
import com.project.workplatform.data.response.Topic.TopicResponse;
import com.project.workplatform.pojo.Topic;
import com.project.workplatform.pojo.TopicComment;
import com.project.workplatform.util.DateFormatUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
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

    @Autowired
    private TopicCommentMapper commentMapper;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

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
            record.setLikeNum(getLikeNum(topic.getId()));
            record.setCommentNum(topic.getCommentNum());
            record.setCreateTime(DateFormatUtil.getStringDateByDate(
                    topic.getCreateTime(),DateFormatUtil.MINUTE_FORMAT));
            record.setLike(isLike(userId,topic.getId()));

            topicList.add(record);
        }
        return topicList;
    }

    public void likeOrNot(Integer userId, int topicId) {
        String redisKey = Constant.REDIS_LIKE_KEY_PREFIX + topicId;
        //检查用户是否点赞
        boolean isLike = isLike(userId,topicId);
        if (isLike){
            redisTemplate.opsForSet().remove(redisKey,userId);
        }else {
            redisTemplate.opsForSet().add(redisKey,userId);
        }
    }

    public void comment(Integer userId, PublishCommentRequest publishCommentRequest) {
        TopicComment comment = new TopicComment();
        comment.setTopicId(publishCommentRequest.getTopicId());
        comment.setPublisherId(userId);
        comment.setTargetId(publishCommentRequest.getTargetId());
        comment.setCommentContent(publishCommentRequest.getCommentContent());
        commentMapper.insertSelective(comment);
    }

    public List<CommentResponse> getCommentList(int topicId) {
        return commentMapper.selectByTopicId(topicId);
    }

    /**
     * 点赞数目前不可能超过2^16，因此转换为int类型返回即可
     */
    private int getLikeNum(int topicId){
        String redisKey = Constant.REDIS_LIKE_KEY_PREFIX + topicId;
        Long likeCount = redisTemplate.opsForSet().size(redisKey);
        return likeCount == null ? 0 : Math.toIntExact(likeCount);
    }

    private boolean isLike(int userId,int topicId){
        String redisKey = Constant.REDIS_LIKE_KEY_PREFIX + topicId;
        Boolean like = redisTemplate.opsForSet().isMember(redisKey, userId);
        return like == null ? false : like;
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
