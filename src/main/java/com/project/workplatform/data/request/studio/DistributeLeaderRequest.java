package com.project.workplatform.data.request.studio;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: Mercer JR
 * @Date: 2022/1/23 14:10
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DistributeLeaderRequest {

    private int leaderId;

    private int studioId;

    private int departmentId;

}
