package com.project.workplatform.data.request.group;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: Mercer JR
 * @Date: 2022/1/16 13:30
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApproveApplyRequest {

    private int applyId;

    private boolean response;

}
