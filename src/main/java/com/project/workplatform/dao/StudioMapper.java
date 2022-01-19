package com.project.workplatform.dao;

import com.project.workplatform.pojo.Studio;

/**
 * @Author: Mercer JR
 * @Date: 2022/1/19 17:17
 */
public interface StudioMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Studio record);

    int insertSelective(Studio record);

    Studio selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Studio record);

    int updateByPrimaryKey(Studio record);
}