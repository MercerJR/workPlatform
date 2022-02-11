package com.project.workplatform.pojo;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: Mercer JR
 * @Date: 2022/1/23 14:27
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserStudio implements Serializable {
    private Integer id;

    private Integer userId;

    private Integer studioId;

    private Integer departmentId;

    /**
     * 0：部门成员，1：部门负责人，2：超级管理员
     */
    private Integer roleId;

    /**
     * 用户工作室内部化名
     */
    private String insideAlias;

    private static final long serialVersionUID = 1L;
}