package com.project.workplatform.pojo;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: Mercer JR
 * @Date: 2022/3/4 10:11
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudioContactInfo implements Serializable {
    private Integer id;

    private Integer studioId;

    private String contactName;

    private String contactPhone;

    private String contactMail;

    private String studioPlace;

    private static final long serialVersionUID = 1L;
}