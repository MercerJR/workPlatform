package com.project.workplatform.dao;

import com.project.workplatform.pojo.UploadFile;

/**
 * @author zengjingran
 */
public interface FileMapper {

    int deleteByPrimaryKey(Integer id);

    int insert(UploadFile record);

    int insertSelective(UploadFile record);

    UploadFile selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(UploadFile record);

    int updateByPrimaryKey(UploadFile record);

}
