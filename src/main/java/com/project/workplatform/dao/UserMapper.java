package com.project.workplatform.dao;

import com.project.workplatform.data.response.search.SearchUserResponse;
import com.project.workplatform.pojo.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author: Mercer JR
 * @Date: 2022/1/8 10:34
 */
public interface UserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);

    User selectByPhoneAndPass(@Param("phoneNumber") String phoneNumber, @Param("password") String password);

    User selectByPhone(String phoneNumber);

    int checkPasswordCorrect(@Param("id") Integer id, @Param("oldPassword") String oldPassword);

    int updatePasswordByPrimaryKey(@Param("id") Integer id, @Param("newPassword") String newPassword);

    List<SearchUserResponse> selectUserInfoByPhone(String phoneNumber);

    List<SearchUserResponse> selectUserInfoByNameFuzzy(String fuzzyName);
}