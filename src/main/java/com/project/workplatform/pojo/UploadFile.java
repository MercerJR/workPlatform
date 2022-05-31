package com.project.workplatform.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zengjingran
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UploadFile {

    private Integer id;

    private String fileFullName;

    private Integer userId;

    private String readerId;

}
