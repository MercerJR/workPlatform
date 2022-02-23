package com.project.workplatform.data.response.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: Mercer JR
 * @Date: 2022/2/23 11:19
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponse {

    private int userId;

    private String name;

    private String token;

}
