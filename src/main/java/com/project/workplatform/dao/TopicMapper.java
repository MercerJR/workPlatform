package com.project.workplatform.dao;

import com.project.workplatform.pojo.Topic;

import java.util.List;

/**
 * @Author: Mercer JR
 * @Date: 2022/1/18 12:01
 */
public interface TopicMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Topic record);

    int insertSelective(Topic record);

    Topic selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Topic record);

    int updateByPrimaryKey(Topic record);

    List<Topic> selectByStudio(int studioId);
}