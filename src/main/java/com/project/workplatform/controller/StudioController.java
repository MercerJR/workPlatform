package com.project.workplatform.controller;

import com.project.workplatform.data.request.studio.CreateStudioRequest;
import com.project.workplatform.data.response.Response;
import com.project.workplatform.service.StudioService;
import com.project.workplatform.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * @Author: Mercer JR
 * @Date: 2022/1/19 16:07
 */
@RestController
@Validated
@RequestMapping("/studio")
public class StudioController {

    @Autowired
    private StudioService service;

    @PostMapping(value = "/create",produces = "application/json")
    public Response create(@Valid @RequestBody CreateStudioRequest createStudioRequest, HttpServletRequest request){
        Integer userId = JwtUtil.getId(request);
        service.create(userId,createStudioRequest);
        return new Response().success();
    }

}
