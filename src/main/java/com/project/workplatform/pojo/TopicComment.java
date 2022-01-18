package com.project.workplatform.pojo;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: Mercer JR
 * @Date: 2022/1/18 16:55
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TopicComment implements Serializable {
    private Integer id;

    private Integer topicId;

    private Integer publisherId;

    private Integer targetId;

    private String commentContent;

    private static final long serialVersionUID = 1L;
}