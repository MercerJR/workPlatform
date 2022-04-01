package com.project.workplatform.pojo;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: Mercer JR
 * @Date: 2022/4/1 9:06
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PersonalMsgRecord implements Serializable {
    private Integer id;

    private Integer senderId;

    private Integer targetId;

    private String time;

    private String content;

    private static final long serialVersionUID = 1L;
}