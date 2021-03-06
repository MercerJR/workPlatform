package com.project.workplatform.pojo;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: Mercer JR
 * @Date: 2022/3/13 16:53
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserInfo implements Serializable {
    private Integer id;

    private Integer userId;

    private String name;

    private String phoneNumber;

    private String describe;

    private String gender;

    private String hobby;

    private String livePlace;

    private String hometown;

    private static final long serialVersionUID = 1L;
}