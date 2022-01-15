package com.project.workplatform.controller;

import com.project.workplatform.data.request.group.ApplyJoinGroupRequest;
import com.project.workplatform.data.request.group.CreateGroupRequest;
import com.project.workplatform.data.request.group.UpdateGroupRequest;
import com.project.workplatform.data.response.Response;
import com.project.workplatform.service.GroupService;
import com.project.workplatform.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * @Author: Mercer JR
 * @Date: 2022/1/15 15:19
 */
@RestController
@Validated
@RequestMapping("/group")
public class GroupController {

    @Autowired
    private GroupService service;

    @PostMapping(value = "/create",produces = "application/json")
    public Response create(@Valid @RequestBody CreateGroupRequest createGroupRequest, HttpServletRequest request){
        Integer userId = JwtUtil.getId(request);
        service.createGroup(userId,createGroupRequest);
        return new Response().success();
    }

    @PostMapping(value = "/update_group_info",produces = "application/json")
    public Response updateGroupInfo(@Valid @RequestBody UpdateGroupRequest updateGroupRequest,HttpServletRequest request){
        Integer userId = JwtUtil.getId(request);
        service.updateGroupInfo(userId,updateGroupRequest);
        return new Response().success();
    }

    @PostMapping(value = "/apply_join",produces = "application/json")
    public Response applyJoin(@Valid @RequestBody ApplyJoinGroupRequest applyJoinGroupRequest,HttpServletRequest request){
        Integer userId = JwtUtil.getId(request);
        service.applyJoin(userId,applyJoinGroupRequest);
        return new Response().success();
    }

}
