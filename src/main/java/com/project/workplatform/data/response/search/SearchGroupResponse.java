package com.project.workplatform.data.response.search;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: Mercer JR
 * @Date: 2022/2/18 18:44
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearchGroupResponse {

    private int groupId;

    private String groupName;

    private String type;

    private String classify;

    private int peopleNumber;

    private String createTime;

}
