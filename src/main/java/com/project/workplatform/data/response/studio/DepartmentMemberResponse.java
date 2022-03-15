package com.project.workplatform.data.response.studio;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: Mercer JR
 * @Date: 2022/3/13 16:32
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DepartmentMemberResponse {

    private int userId;

    private String insideAlias;

    private String phoneNumber;

    private String departmentName;

}
