package com.project.workplatform.data.response.studio;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: Mercer JR
 * @Date: 2022/2/23 12:38
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudioBaseInfoResponse {

    private int studioId;

    private String studioName;

    private String studioAbbreviation;
    
    private String role;

    private String departmentName;

}
