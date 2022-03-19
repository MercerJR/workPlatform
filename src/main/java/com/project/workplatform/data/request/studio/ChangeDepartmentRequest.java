package com.project.workplatform.data.request.studio;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: Mercer JR
 * @Date: 2022/3/19 12:11
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChangeDepartmentRequest {

    private Integer userId;

    private Integer oldDepartmentId;

    private String departmentName;

    private Integer studioId;

}
