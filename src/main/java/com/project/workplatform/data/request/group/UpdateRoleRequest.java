package com.project.workplatform.data.request.group;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: Mercer JR
 * @Date: 2022/1/17 18:24
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateRoleRequest {

    private int memberId;

    private int groupId;

    private int roleId;

}
