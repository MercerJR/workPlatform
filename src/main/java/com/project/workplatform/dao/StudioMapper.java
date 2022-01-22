package com.project.workplatform.dao;

import com.project.workplatform.data.response.studio.StudioInfoResponse;
import com.project.workplatform.pojo.Studio;
import org.apache.ibatis.annotations.Param;

/**
 * @Author: Mercer JR
 * @Date: 2022/1/21 14:16
 */
public interface StudioMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Studio record);

    int insertSelective(Studio record);

    Studio selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Studio record);

    int updateByPrimaryKey(Studio record);

    int selectCreatorByPrimaryKey(int id);

    int updateInviteCodeByPrimaryKey(@Param("inviteCode") String inviteCode, @Param("id") int id);

    StudioInfoResponse selectStudioInfoByPrimaryKey(int id);
}