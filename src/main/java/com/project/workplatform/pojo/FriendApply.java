package com.project.workplatform.pojo;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: Mercer JR
 * @Date: 2022/1/12 19:37
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FriendApply implements Serializable {
    private Integer id;

    private Integer userId;

    private Integer targetId;

    private Integer tag;

    private String applyMessage;

    private static final long serialVersionUID = 1L;
}