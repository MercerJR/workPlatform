package com.project.workplatform.data.request.studio;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: Mercer JR
 * @Date: 2022/1/23 11:55
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateDepartmentRequest {

    private String departmentName;

    private int studioId;

    private int parentDepartmentId;

    private String type;

}
