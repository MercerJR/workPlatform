package com.project.workplatform.pojo;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: Mercer JR
 * @Date: 2022/1/23 13:29
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudioDepartment implements Serializable {
    private Integer id;

    private String departmentName;

    private Integer studioId;

    private Integer parentDepartmentId;

    private Integer leaderId;

    private String type;

    private Integer peopleNumber;

    private static final long serialVersionUID = 1L;
}