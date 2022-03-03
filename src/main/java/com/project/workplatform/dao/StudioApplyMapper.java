package com.project.workplatform.dao;

import com.project.workplatform.pojo.StudioApply;

/**
 * @Author: Mercer JR
 * @Date: 2022/1/22 12:53
 */
public interface StudioApplyMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(StudioApply record);

    int insertSelective(StudioApply record);

    StudioApply selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(StudioApply record);

    int updateByPrimaryKey(StudioApply record);

    Integer selectNotActivatedNumberByStudio(Integer studioId);
}