package com.project.workplatform.data.request.group;

import com.project.workplatform.data.ValidConstant;
import com.project.workplatform.exception.ExceptionMessage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Size;

/**
 * @Author: Mercer JR
 * @Date: 2022/1/15 17:26
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApplyJoinGroupRequest {

    private int groupId;

    @Size(max = 100,message = ValidConstant.APPLY_MESSAGE_LENGTH)
    private String applyMessage;

}
