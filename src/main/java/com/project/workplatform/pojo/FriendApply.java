package com.project.workplatform.pojo;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: Mercer JR
 * @Date: 2022/1/11 10:53
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FriendApply implements Serializable {
    private Integer id;

    private Integer userId;

    private Integer targetId;

    private Integer tag;

    private static final long serialVersionUID = 1L;
}