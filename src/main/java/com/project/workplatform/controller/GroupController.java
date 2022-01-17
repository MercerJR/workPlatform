package com.project.workplatform.controller;

import com.project.workplatform.data.request.group.ApplyJoinGroupRequest;
import com.project.workplatform.data.request.group.ApproveApplyRequest;
import com.project.workplatform.data.request.group.CreateGroupRequest;
import com.project.workplatform.data.request.group.UpdateGroupRequest;
import com.project.workplatform.data.response.Response;
import com.project.workplatform.data.response.group.ApplyUserResponse;
import com.project.workplatform.data.response.group.GroupInfoResponse;
import com.project.workplatform.data.response.group.GroupResponse;
import com.project.workplatform.data.response.group.MemberResponse;
import com.project.workplatform.service.GroupService;
import com.project.workplatform.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

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

    @GetMapping(value = "/apply_list/{group_id}",produces = "application/json")
    public Response getApplyList(@PathVariable("group_id") Integer groupId,HttpServletRequest request){
        Integer userId = JwtUtil.getId(request);
        List<ApplyUserResponse> list = service.getApplyList(userId,groupId);
        return new Response().success(list);
    }

    @PostMapping(value = "/approve_apply",produces = "application/json")
    public Response approveApply(@Valid @RequestBody ApproveApplyRequest approveApplyRequest,HttpServletRequest request){
        Integer userId = JwtUtil.getId(request);
        service.approveApply(userId,approveApplyRequest);
        return new Response().success();
    }

    @GetMapping(value = "/show_group_info/{group_id}",produces = "application/json")
    public Response showGroupInfo(@PathVariable("group_id")int groupId){
        GroupInfoResponse groupInfo = service.getGroupInfo(groupId);
        return new Response().success(groupInfo);
    }

    @GetMapping(value = "/show_member_list/{group_id}",produces = "application/json")
    public Response showMemberList(@PathVariable("group_id")int groupId,HttpServletRequest request){
        Integer userId = JwtUtil.getId(request);
        List<MemberResponse> list = service.getMemberList(userId,groupId);
        return new Response().success(list);
    }

    @GetMapping(value = "/show_group_list",produces = "application/json")
    public Response showGroupList(HttpServletRequest request){
        Integer userId = JwtUtil.getId(request);
        List<GroupResponse> list = service.getGroupList(userId);
        return new Response().success(list);
    }

}
