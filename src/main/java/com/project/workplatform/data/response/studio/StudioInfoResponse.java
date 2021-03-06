package com.project.workplatform.data.response.studio;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: Mercer JR
 * @Date: 2022/1/22 13:39
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudioInfoResponse {

    private int studioId;

    private String studioName;

    private String studioAbbreviation;

    private String creatorAlias;

    private String classify;

    private String describe;

    private int peopleNumber;

}
