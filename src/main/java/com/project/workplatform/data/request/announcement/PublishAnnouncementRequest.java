package com.project.workplatform.data.request.announcement;

import com.project.workplatform.data.ValidConstant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotBlank;

import java.util.Set;

/**
 * @author zengjingran
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PublishAnnouncementRequest {

    @NotBlank(message = ValidConstant.TITLE_EMPTY)
    private String title;

    @NotBlank(message = ValidConstant.CONTENT_EMPTY)
    private String content;

    private Integer studioId;

    private Set<Integer> memberSet;

}
