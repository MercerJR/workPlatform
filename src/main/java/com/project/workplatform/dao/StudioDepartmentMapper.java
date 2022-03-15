package com.project.workplatform.dao;

import com.project.workplatform.pojo.StudioDepartment;import org.apache.ibatis.annotations.Param;

/**
 * @Author: Mercer JR
 * @Date: 2022/3/13 12:07
 */
public interface StudioDepartmentMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(StudioDepartment record);

    int insertSelective(StudioDepartment record);

    StudioDepartment selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(StudioDepartment record);

    int updateByPrimaryKey(StudioDepartment record);

    int updateLeaderByStudioAndDepartment(@Param("leaderId") int leaderId, @Param("studioId") int studioId, @Param("departmentId") int departmentId);

    Integer selectDepartmentNumberByStudio(Integer studioId);

    StudioDepartment selectDepartmentByStudioAndParent(@Param("studioId") int studioId, @Param("parentDepartmentId") int parentDepartmentId);

    Integer selectIdByDepartmentName(String parentDepartmentName);
}