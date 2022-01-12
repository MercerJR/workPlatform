package com.project.workplatform.data.request.user;

import com.project.workplatform.annotion.IsPhoneNumber;
import com.project.workplatform.data.ValidConstant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Size;

/**
 * @Author: Mercer JR
 * @Date: 2022/1/8 10:44
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserLoginRequest {

    @NotEmpty(message = ValidConstant.PHONE_NUMBER_EMPTY)
    @IsPhoneNumber
    private String phoneNumber;

    @NotBlank(message = ValidConstant.PASSWORD_EMPTY)
    @Size(min = 8,message = ValidConstant.PASSWORD_LENGTH)
    private String password;
}
