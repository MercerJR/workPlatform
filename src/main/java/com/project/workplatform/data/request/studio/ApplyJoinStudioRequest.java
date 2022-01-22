package com.project.workplatform.data.request.studio;

import com.project.workplatform.data.ValidConstant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Size;

/**
 * @Author: Mercer JR
 * @Date: 2022/1/20 13:18
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApplyJoinStudioRequest {

    private int studioId;

    @Size(max = 50,message = ValidConstant.STUDIO_APPLY_MESSAGE_LENGTH)
    private String applyMessage;

}
