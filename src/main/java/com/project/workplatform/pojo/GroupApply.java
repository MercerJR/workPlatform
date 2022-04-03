package com.project.workplatform.pojo;

import java.io.Serializable;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: Mercer JR
 * @Date: 2022/4/3 15:55
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GroupApply implements Serializable {
    private Integer id;

    private Integer userId;

    private Integer groupId;

    private String applyMessage;

    /**
     * 0为未处理，1为同意，2为拒绝
     */
    private Integer tag;

    private Date createTime;

    private static final long serialVersionUID = 1L;
}