package com.project.workplatform.data.request.studio;

import com.project.workplatform.data.ValidConstant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Size;

/**
 * @Author: Mercer JR
 * @Date: 2022/1/19 17:20
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateStudioRequest {

    @NotBlank(message = ValidConstant.STUDIO_NAME_EMPTY)
    @Size(max = 20,message = ValidConstant.STUDIO_NAME_LENGTH)
    private String studioName;

    private String studioAbbreviation;

    private String classify;

}
