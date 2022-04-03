package com.project.workplatform.pojo;

import java.io.Serializable;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: Mercer JR
 * @Date: 2022/4/3 15:27
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FriendApply implements Serializable {
    private Integer id;

    private Integer userId;

    private Integer targetId;

    /**
     * 0为未处理，1为同意，2为拒绝
     */
    private Integer tag;

    private String applyMessage;

    private Date createTime;

    private static final long serialVersionUID = 1L;
}