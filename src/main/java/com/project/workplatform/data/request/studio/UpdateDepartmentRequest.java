package com.project.workplatform.data.request.studio;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: Mercer JR
 * @Date: 2022/3/19 13:54
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateDepartmentRequest {

    private Integer departmentId;

    private Integer studioId;

    private String departmentName;

    private String parentDepartmentName;

}
