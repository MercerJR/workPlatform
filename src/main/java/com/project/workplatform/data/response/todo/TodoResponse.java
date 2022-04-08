package com.project.workplatform.data.response.todo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zengjingran
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TodoResponse {

    private Integer todoId;

    private String title;

    private String describe;

    private String timePeriod;

    private Integer originatorId;

    private String originator;

    private String participants;

}
