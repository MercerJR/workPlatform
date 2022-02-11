package com.project.workplatform.dao;

import com.project.workplatform.pojo.UserStudio;import org.apache.ibatis.annotations.Param;

/**
 * @Author: Mercer JR
 * @Date: 2022/1/23 14:27
 */
public interface UserStudioMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(UserStudio record);

    int insertSelective(UserStudio record);

    UserStudio selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(UserStudio record);

    int updateByPrimaryKey(UserStudio record);

    UserStudio selectByUserAndStudio(@Param("userId") int userId, @Param("studioId") int studioId);

    void updateDepartmentInfoByUserAndStudio(@Param("departmentId") int departmentId,@Param("roleId") int roleId,
                                             @Param("userId") int userId, @Param("studioId") int studioId);
}