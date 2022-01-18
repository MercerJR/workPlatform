package com.project.workplatform.pojo;

import java.io.Serializable;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: Mercer JR
 * @Date: 2022/1/18 12:01
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Topic implements Serializable {
    private Integer id;

    private Integer publisherId;

    private String title;

    private String content;

    private String img;

    private String label;

    private Integer likeNum;

    private Integer commentNum;

    private Date createTime;

    private Integer studioId;

    private static final long serialVersionUID = 1L;
}