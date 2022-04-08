package com.project.workplatform.dao;

import com.project.workplatform.pojo.Todo;

/**
 * @Author: Mercer JR
 * @Date: 2022/4/8 20:20
 */
public interface TodoMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Todo record);

    int insertSelective(Todo record);

    Todo selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Todo record);

    int updateByPrimaryKey(Todo record);
}