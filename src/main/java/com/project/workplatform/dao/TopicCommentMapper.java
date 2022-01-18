package com.project.workplatform.dao;

import com.project.workplatform.data.response.Topic.CommentResponse;
import com.project.workplatform.pojo.TopicComment;

import java.util.List;

/**
 * @Author: Mercer JR
 * @Date: 2022/1/18 16:55
 */
public interface TopicCommentMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(TopicComment record);

    int insertSelective(TopicComment record);

    TopicComment selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(TopicComment record);

    int updateByPrimaryKey(TopicComment record);

    List<CommentResponse> selectByTopicId(Integer topicId);
}