package com.project.workplatform.pojo;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: Mercer JR
 * @Date: 2022/3/13 12:07
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudioDepartment implements Serializable {
    private Integer id;

    private String departmentName;

    private Integer studioId;

    /**
     * 顶级部门的父部门id为0
     */
    private Integer parentDepartmentId;

    /**
     * 子部门id，通过“,”分割
     */
    private String childrenDepartmentId;

    private String type;

    private Integer peopleNumber;

    private static final long serialVersionUID = 1L;
}