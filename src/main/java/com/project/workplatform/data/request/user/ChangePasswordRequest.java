package com.project.workplatform.data.request.user;

import com.project.workplatform.data.ValidConstant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Size;

/**
 * @Author: Mercer JR
 * @Date: 2022/1/9 10:47
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChangePasswordRequest {

    @NotBlank(message = ValidConstant.PASSWORD_EMPTY)
    private String oldPassword;

    @NotBlank(message = ValidConstant.NEW_PASSWORD_EMPTY)
    @Size(min = 8,message = ValidConstant.PASSWORD_LENGTH)
    private String newPassword;

    @NotBlank(message = ValidConstant.NEW_PASSWORD_EMPTY)
    @Size(min = 8,message = ValidConstant.PASSWORD_LENGTH)
    private String newPasswordAgain;

}
