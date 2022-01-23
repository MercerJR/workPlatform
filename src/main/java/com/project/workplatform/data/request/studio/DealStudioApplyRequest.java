package com.project.workplatform.data.request.studio;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: Mercer JR
 * @Date: 2022/1/22 14:06
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DealStudioApplyRequest {

    private int applyId;

    private boolean agree;

    private int departmentId;

    private int adminTag;

}
