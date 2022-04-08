package com.project.workplatform.data.response.todo;

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
public class TodoPageResponse {

    private List<TodoResponse> todoList;

    private List<TodoResponse> currentTodoList;

}
