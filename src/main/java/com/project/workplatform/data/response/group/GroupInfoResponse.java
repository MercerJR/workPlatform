package com.project.workplatform.data.response.group;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: Mercer JR
 * @Date: 2022/1/16 18:32
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GroupInfoResponse {

    private String groupName;

    private String type;

    private String studio;

    private String classify;

    private String createTime;

}
