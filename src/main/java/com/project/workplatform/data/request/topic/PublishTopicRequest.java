package com.project.workplatform.data.request.topic;

import com.project.workplatform.data.ValidConstant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Size;
import java.util.List;

/**
 * @Author: Mercer JR
 * @Date: 2022/1/18 10:32
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PublishTopicRequest {

    @Size(max = 20,message = ValidConstant.TITLE_LENGTH)
    private String title;

    @NotBlank(message = ValidConstant.CONTENT_EMPTY)
    private String content;

    private List<String> imgList;

    private List<String> labelList;

    private int studioId;

}
