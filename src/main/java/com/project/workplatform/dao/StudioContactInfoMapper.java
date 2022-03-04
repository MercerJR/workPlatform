package com.project.workplatform.dao;

import com.project.workplatform.data.response.studio.StudioContactInfoResponse;
import com.project.workplatform.pojo.StudioContactInfo;

/**
 * @Author: Mercer JR
 * @Date: 2022/3/4 10:11
 */
public interface StudioContactInfoMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(StudioContactInfo record);

    int insertSelective(StudioContactInfo record);

    StudioContactInfo selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(StudioContactInfo record);

    int updateByPrimaryKey(StudioContactInfo record);

    StudioContactInfoResponse selectByStudio(Integer studioId);

    void updateByStudioSelective(StudioContactInfo contactInfo);
}