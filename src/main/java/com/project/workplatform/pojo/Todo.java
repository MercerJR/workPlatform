package com.project.workplatform.pojo;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: Mercer JR
 * @Date: 2022/4/8 20:20
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Todo implements Serializable {
    private Integer id;

    private String titile;

    private String describe;

    private String day;

    private String startTime;

    private String endTime;

    private Integer originatorId;

    private Integer participantId;

    private Integer studioId;

    private static final long serialVersionUID = 1L;
}