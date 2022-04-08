package com.project.workplatform.data.request.todo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zengjingran
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeleteTodoRequest {

    private Integer todoId;

}
