package com.project.workplatform.pojo;

import java.io.Serializable;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: Mercer JR
 * @Date: 2022/4/7 23:31
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Announcement implements Serializable {
    private Integer id;

    private String title;

    private String content;

    private Integer publisherId;

    private String readerId;

    private Date time;

    private Integer studioId;

    private static final long serialVersionUID = 1L;
}