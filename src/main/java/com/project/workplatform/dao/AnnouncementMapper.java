package com.project.workplatform.dao;

import com.project.workplatform.pojo.Announcement;

/**
 * @Author: Mercer JR
 * @Date: 2022/4/7 22:41
 */
public interface AnnouncementMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Announcement record);

    int insertSelective(Announcement record);

    Announcement selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Announcement record);

    int updateByPrimaryKey(Announcement record);
}