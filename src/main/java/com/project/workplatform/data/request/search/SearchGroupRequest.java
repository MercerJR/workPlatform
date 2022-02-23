package com.project.workplatform.data.request.search;

import com.project.workplatform.data.ValidConstant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Size;

/**
 * @Author: Mercer JR
 * @Date: 2022/2/18 18:34
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearchGroupRequest {

    @NotBlank(message = ValidConstant.SEARCH_CONTENT_EMPTY)
    @Size(max = 20,message = ValidConstant.SEARCH_CONTENT_LENGTH)
    private String searchContent;

}
