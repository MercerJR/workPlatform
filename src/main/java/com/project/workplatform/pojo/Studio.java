package com.project.workplatform.pojo;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: Mercer JR
 * @Date: 2022/4/7 22:28
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Studio implements Serializable {
    private Integer id;

    private String studioName;

    private String studioAbbreviation;

    private Integer creatorId;

    private String classify;

    private String describe;

    private Integer peopleNumber;

    private String inviteCode;

    private Integer helperId;

    private static final long serialVersionUID = 1L;
}