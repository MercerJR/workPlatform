package com.project.workplatform.pojo;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: Mercer JR
 * @Date: 2022/1/16 20:23
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserGroup implements Serializable {
    private Integer id;

    private Integer userId;

    private Integer groupId;

    private String nickname;

    /**
     * 0：普通成员，1：管理员，2：群主
     */
    private Integer roleId;

    private static final long serialVersionUID = 1L;
}