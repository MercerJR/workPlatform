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
public class StudioApply implements Serializable {
    private Integer id;

    private Integer userId;

    private Integer studioId;

    private String applyMessage;

    private Integer departmentId;

    /**
     * 0：自己申请；1：工作室部门负责人邀请
     */
    private Integer applyTag;

    /**
     * 0：未处理；1：同意；2：拒绝
     */
    private Integer tag;

    private Date createTime;

    private static final long serialVersionUID = 1L;
}