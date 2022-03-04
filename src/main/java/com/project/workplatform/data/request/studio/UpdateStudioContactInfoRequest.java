package com.project.workplatform.data.request.studio;

import com.project.workplatform.annotion.IsMail;
import com.project.workplatform.annotion.IsPhoneNumber;
import com.project.workplatform.data.ValidConstant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Size;

/**
 * @Author: Mercer JR
 * @Date: 2022/3/4 19:34
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateStudioContactInfoRequest {

    private int studioId;

    @Size(max = 10,message = ValidConstant.STUDIO_CONTACT_NAME_LENGTH)
    private String contactName;

    @IsMail
    private String contactMail;

    @IsPhoneNumber
    private String contactPhone;

    private String studioPlace;

}
