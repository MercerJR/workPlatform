package com.project.workplatform.data.request.studio;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: Mercer JR
 * @Date: 2022/1/21 13:50
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class InitInviteCodeRequest {

    private int studioId;

    private int expireDay;

}
