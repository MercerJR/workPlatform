package com.project.workplatform.data.request.studio;

import com.project.workplatform.data.ValidConstant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Size;

/**
 * @Author: Mercer JR
 * @Date: 2022/3/4 10:35
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateStudioInfoRequest {

    private int studioId;

    @NotBlank(message = ValidConstant.STUDIO_NAME_EMPTY)
    @Size(max = 20,message = ValidConstant.STUDIO_NAME_LENGTH)
    private String studioName;

    @Size(min = 0,max = 20,message = ValidConstant.STUDIO_ABBREVIATION_LENGTH)
    private String studioAbbreviation;

    @Size(min = 0,max = 50,message = ValidConstant.STUDIO_CLASSIFY_LENGTH)
    private String classify;

    private String describe;

}
