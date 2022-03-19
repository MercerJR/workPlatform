package com.project.workplatform.data.request.studio;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: Mercer JR
 * @Date: 2022/3/19 15:30
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeleteDepartmentRequest {

    private Integer departmentId;

    private Integer studioId;

}
