package com.project.workplatform.data.response.group;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: Mercer JR
 * @Date: 2022/1/16 19:43
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MemberResponse {

    private int userId;

    private String nickname;

    private String name;

    private int roleId;

    private String role;

}
