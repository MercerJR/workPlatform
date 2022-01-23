package com.project.workplatform.pojo;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: Mercer JR
 * @Date: 2022/1/22 12:53
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

    private static final long serialVersionUID = 1L;
}