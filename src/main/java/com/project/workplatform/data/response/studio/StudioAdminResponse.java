package com.project.workplatform.data.response.studio;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: Mercer JR
 * @Date: 2022/3/7 10:33
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudioAdminResponse {

    private int userId;

    private int studioId;

    private String insideAlias;

    private String role;

    private String phoneNumber;

}
