package com.project.workplatform.pojo;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: Mercer JR
 * @Date: 2022/1/15 17:25
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserGroup implements Serializable {
    private Integer id;

    private Integer userId;

    private Integer groupId;

    private String nickname;

    private static final long serialVersionUID = 1L;
}