package com.project.workplatform.data.request.topic;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: Mercer JR
 * @Date: 2022/1/18 16:38
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PublishCommentRequest {

    private int topicId;

    private int targetId;

    private String commentContent;

}
