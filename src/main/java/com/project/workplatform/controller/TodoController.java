package com.project.workplatform.controller;

import com.project.workplatform.data.request.todo.AddTodoRequest;
import com.project.workplatform.data.request.todo.DeleteTodoRequest;
import com.project.workplatform.data.response.Response;
import com.project.workplatform.data.response.todo.TodoPageResponse;
import com.project.workplatform.service.TodoService;
import com.project.workplatform.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * @author zengjingran
 */
@RestController
@Validated
@RequestMapping("/todo")
public class TodoController {

    @Autowired
    private TodoService service;

    @PostMapping(value = "/add",produces = "application/json")
    public Response add(@Valid @RequestBody AddTodoRequest addTodoRequest, HttpServletRequest request){
        Integer userId = JwtUtil.getId(request);
        service.addTodo(addTodoRequest,userId);
        return new Response().success();
    }

    @PostMapping(value = "/delete",produces = "application/json")
    public Response delete(@RequestBody DeleteTodoRequest deleteTodoRequest, HttpServletRequest request){
        Integer userId = JwtUtil.getId(request);
        service.deleteTodo(deleteTodoRequest,userId);
        return new Response().success();
    }

    @GetMapping(value = "/show_todo_list/{studio_id}",produces = "application/json")
    public Response getTodoList(@PathVariable("studio_id") Integer studioId,HttpServletRequest request){
        Integer userId = JwtUtil.getId(request);
        TodoPageResponse response = service.getTodoList(studioId,userId);
        return new Response().success(response);
    }

}
