package com.project.workplatform.dao;

import com.project.workplatform.data.response.studio.DepartmentMemberResponse;
import com.project.workplatform.data.response.studio.StudioAdminResponse;
import com.project.workplatform.pojo.UserStudio;
import org.apache.ibatis.annotations.Param;

import java.util.List;

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

    void updateDepartmentInfoByUserAndStudio(@Param("departmentId") int departmentId, @Param("roleId") int roleId,
                                             @Param("userId") int userId, @Param("studioId") int studioId);

    Integer selectMemberNumberByStudio(Integer studioId);

    Integer selectSuperAdminNumberByStudio(Integer studioId);

    Integer selectAdminNumberByStudio(Integer studioId);

    List<StudioAdminResponse> selectAdminByStudio(@Param("studioId") Integer studioId, @Param("roleId") Integer roleId);

    List<StudioAdminResponse> selectAdminByStudioAndPhone(@Param("studioId") Integer studioId,
                                                          @Param("roleId") Integer roleId,@Param("phoneNumber") String phoneNumber);

    List<StudioAdminResponse> selectAdminByStudioAndNameFuzzy(@Param("studioId") Integer studioId,
                                                              @Param("roleId") Integer roleId,@Param("nameFuzzy") String nameFuzzy);

    void updateRoleByUserAndStudio(@Param("userId") Integer userId,@Param("studioId") Integer studioId,@Param("roleId") Integer roleId);

    List<StudioAdminResponse> selectAdminByDepartmentNameFuzzy(@Param("studioId") Integer studioId,
                                          @Param("roleId") Integer roleId,@Param("departmentNameFuzzy") String departmentNameFuzzy);

    List<String> selectLeaderNameByDepartment(Integer departmentId);

    List<DepartmentMemberResponse> selectMemberByDepartment(int departmentId);

    List<UserStudio> selectByDepartmentId(Integer departmentId);
}