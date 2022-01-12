package com.project.workplatform.data.request.friend;

import com.project.workplatform.data.ValidConstant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Size;

/**
 * @Author: Mercer JR
 * @Date: 2022/1/9 14:49
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApplyFriendRequest {

    private int targetId;

    @Size(max = 50,message = ValidConstant.APPLY_MESSAGE_LENGTH)
    private String applyMessage;

}
