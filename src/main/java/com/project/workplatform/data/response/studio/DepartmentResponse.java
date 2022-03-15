package com.project.workplatform.data.response.studio;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: Mercer JR
 * @Date: 2022/3/13 11:59
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DepartmentResponse {

    private int index;

    private int departmentId;

    private String departmentName;

    private int peopleNumber;

    private String leader;

}
