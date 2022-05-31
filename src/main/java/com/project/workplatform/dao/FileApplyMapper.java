package com.project.workplatform.dao;

import com.project.workplatform.pojo.FileApply;

/**
 * @author zengjingran
 */
public interface FileApplyMapper {

    int deleteByPrimaryKey(Integer id);

    int insert(FileApply record);

    int insertSelective(FileApply record);

    FileApply selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(FileApply record);

    int updateByPrimaryKey(FileApply record);

}
