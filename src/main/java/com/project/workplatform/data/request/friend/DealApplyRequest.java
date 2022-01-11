package com.project.workplatform.data.request.friend;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: Mercer JR
 * @Date: 2022/1/9 15:03
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DealApplyRequest {

    private int applyId;

    private boolean response;

}
