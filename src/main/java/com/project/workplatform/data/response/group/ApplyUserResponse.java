package com.project.workplatform.data.response.group;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: Mercer JR
 * @Date: 2022/1/16 11:37
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApplyUserResponse {

    private int userId;

    private String name;

    private String applyMessage;

}
