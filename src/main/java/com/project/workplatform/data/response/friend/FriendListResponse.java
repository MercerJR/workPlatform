package com.project.workplatform.data.response.friend;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: Mercer JR
 * @Date: 2022/1/14 10:24
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FriendListResponse {

    private int friendId;

    private String name;

}
