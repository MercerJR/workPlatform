package com.project.workplatform.data.response.studio;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: Mercer JR
 * @Date: 2022/3/3 20:20
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudioContactInfoResponse {

    private int studioId;

    private String contactName;

    private String contactPhone;

    private String contactMail;

    private String studioPlace;

}
