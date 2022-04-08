package com.project.workplatform.data.request.todo;

import com.project.workplatform.data.ValidConstant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.Set;

/**
 * @author zengjingran
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddTodoRequest {

    @NotEmpty(message = ValidConstant.TITLE_EMPTY)
    private String title;

    private String describe;

    @NotNull(message = ValidConstant.DAY_EMPTY)
    private Date day;

    @NotEmpty(message = ValidConstant.TIME_EMPTY)
    private String startTime;

    @NotEmpty(message = ValidConstant.TIME_EMPTY)
    private String endTime;

    private Integer studioId;

    private Set<Integer> memberSet;

}
