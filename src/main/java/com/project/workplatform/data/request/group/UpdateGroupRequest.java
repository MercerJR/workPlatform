package com.project.workplatform.data.request.group;

import com.project.workplatform.data.ValidConstant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Size;

/**
 * @Author: Mercer JR
 * @Date: 2022/1/15 16:49
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateGroupRequest {

    private int groupId;

    @Size(max = 20,message = ValidConstant.GROUP_NAME_LENGTH)
    private String groupName;

    @Size(max = 20,message = ValidConstant.GROUP_CLASSIFY_LENGTH)
    private String classify;

}
