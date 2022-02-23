package com.project.workplatform.data.response.search;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: Mercer JR
 * @Date: 2022/2/18 18:42
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearchUserResponse {

    private int userId;

    private String name;

    private String gender;

    private String hobby;

    private String livePlace;

    private String hometown;

    private String describe;

}
