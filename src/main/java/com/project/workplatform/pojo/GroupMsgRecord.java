package com.project.workplatform.pojo;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: Mercer JR
 * @Date: 2022/4/1 9:07
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GroupMsgRecord implements Serializable {
    private Integer id;

    private Integer senderId;

    private Integer groupId;

    private String time;

    private String content;

    private static final long serialVersionUID = 1L;
}