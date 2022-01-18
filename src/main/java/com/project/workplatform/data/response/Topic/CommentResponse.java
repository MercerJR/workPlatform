package com.project.workplatform.data.response.Topic;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: Mercer JR
 * @Date: 2022/1/18 16:53
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentResponse {

    private int publisherId;

    private String publisherName;

    private int targetId;

    private String targetName;

    private String commentContent;

}
