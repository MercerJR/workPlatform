package com.project.workplatform.pojo;

import java.io.Serializable;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: Mercer JR
 * @Date: 2022/1/15 15:57
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Group implements Serializable {
    private Integer id;

    private String groupName;

    private Integer creatorId;

    /**
     * 1为内部群聊，0为外部群聊
     */
    private Integer type;

    private Integer studioId;

    private String classify;

    private Date createTime;

    private Integer peopleNumber;

    private static final long serialVersionUID = 1L;
}