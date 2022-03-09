package com.project.workplatform.data.request.studio;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: Mercer JR
 * @Date: 2022/3/7 22:03
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateStudioRoleRequest {

    private Integer userId;

    private String insideAlias;

    private Integer studioId;

    private String departmentName;

    private int roleId;

}
