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
public class FileApply {

    private Integer id;

    private Integer fileId;

    private Integer userId;

    private Integer tag;

}
