package com.project.workplatform.data.response.studio;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author zengjingran
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DepartmentMemberTreeResponse {

    private Integer id;

    private String label;

    private List<DepartmentMemberTreeResponse> children;

}
