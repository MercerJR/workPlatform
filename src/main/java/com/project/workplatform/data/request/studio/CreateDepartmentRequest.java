package com.project.workplatform.data.request.studio;

import com.project.workplatform.data.ValidConstant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotBlank;

/**
 * @Author: Mercer JR
 * @Date: 2022/1/23 11:55
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateDepartmentRequest {

    @NotBlank(message = ValidConstant.DEPARTMENT_NAME_EMPTY)
    private String departmentName;

    private int studioId;

    @NotBlank(message = ValidConstant.PARENT_DEPARTMENT_NAME_EMPTY)
    private String parentDepartmentName;

    private String type;

}
