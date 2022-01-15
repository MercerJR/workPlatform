package com.project.workplatform.data.request.group;

import com.project.workplatform.data.ValidConstant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Size;

/**
 * @Author: Mercer JR
 * @Date: 2022/1/15 15:36
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateGroupRequest {

    @Size(max = 20,message = ValidConstant.GROUP_NAME_LENGTH)
    private String groupName;

    /**
     * 内部群聊为true，外部为false
     */
    private boolean isInside;

    @Size(max = 20,message = ValidConstant.GROUP_CLASSIFY_LENGTH)
    private String classify;

}
