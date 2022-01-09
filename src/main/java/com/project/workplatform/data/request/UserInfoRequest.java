package com.project.workplatform.data.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Size;

/**
 * @Author: Mercer JR
 * @Date: 2022/1/8 14:34
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserInfoRequest {

    private Integer userId;

    @Size(max = 2,message = "请输入正确的性别，如“男”、“女”")
    private String gender;

    private String describe;

    private String hobby;

    private String livePlace;

    private String hometown;

}
