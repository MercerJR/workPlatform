package com.project.workplatform.data.request.studio;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: Mercer JR
 * @Date: 2022/1/22 12:51
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class InviteJoinStudioRequest {

    private int studioId;

    private int departmentId;

    private int inviteUserId;

}
