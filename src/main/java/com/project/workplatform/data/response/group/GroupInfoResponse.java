package com.project.workplatform.data.response.group;

import com.fasterxml.jackson.annotation.JsonFormat;
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

    private int groupId;

    private String groupName;

    private String type;

    private String classify;

    private int peopleNumber;

    private int currentUserRoleId;

    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    private String createTime;

}
