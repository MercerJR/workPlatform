package com.project.workplatform.data.request.studio;

import com.project.workplatform.data.ValidConstant;
import com.project.workplatform.exception.ExceptionMessage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Size;

/**
 * @Author: Mercer JR
 * @Date: 2022/1/21 11:41
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CheckInviteCodeRequest {

    @NotBlank(message = ValidConstant.INVALID_CODE_EMPTY)
    @Size(max = 8,message = ValidConstant.INVALID_CODE_LENGTH)
    private String inviteCode;

}
