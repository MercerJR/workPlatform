package com.project.workplatform.data.request.topic;

import com.project.workplatform.data.ValidConstant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotBlank;

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

    @NotBlank(message = ValidConstant.COMMENT_EMPTY)
    private String commentContent;

}
