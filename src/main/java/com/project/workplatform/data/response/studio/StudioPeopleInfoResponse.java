package com.project.workplatform.data.response.studio;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: Mercer JR
 * @Date: 2022/3/2 9:57
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudioPeopleInfoResponse {

    private Integer studioMemberNumber;

    private Integer notActivatedNumber;

    private Integer departmentNumber;

    private Integer superAdminNumber;

    private Integer adminNumber;

}
