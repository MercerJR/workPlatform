package com.project.workplatform.data.response.friend;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: Mercer JR
 * @Date: 2022/2/12 8:53
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class FriendInfoResponse {

    private String name;

    private String describe;

    private String gender;

    private String hobby;

    private String livePlace;

    private String hometown;

}
