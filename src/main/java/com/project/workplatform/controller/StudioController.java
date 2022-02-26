package com.project.workplatform.controller;

import com.project.workplatform.data.request.studio.*;
import com.project.workplatform.data.response.Response;
import com.project.workplatform.data.response.studio.StudioBaseInfoResponse;
import com.project.workplatform.data.response.studio.StudioInfoResponse;
import com.project.workplatform.service.StudioService;
import com.project.workplatform.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;

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

    @PostMapping(value = "/init_invite_code",produces = "application/json")
    public Response initInviteCode(@Valid @RequestBody InitInviteCodeRequest initInviteCodeRequest, HttpServletRequest request){
        Integer userId = JwtUtil.getId(request);
        String inviteCode = service.initInviteCode(userId, initInviteCodeRequest);
        return new Response().success(inviteCode);
    }

    @PostMapping(value = "/check_invite_code",produces = "application/json")
    public Response checkInviteCode(@Valid @RequestBody CheckInviteCodeRequest checkInviteCodeRequest){
        Integer studioId = service.checkInviteCode(checkInviteCodeRequest.getInviteCode());
        return new Response().success(studioId);
    }

    @PostMapping(value = "/apply_join",produces = "application/json")
    public Response applyJoin(@Valid @RequestBody ApplyJoinStudioRequest applyJoinStudioRequest, HttpServletRequest request){
        Integer userId = JwtUtil.getId(request);
        service.applyJoin(userId,applyJoinStudioRequest);
        return new Response().success();
    }

    @PostMapping(value = "/invite_join",produces = "application/json")
    public Response inviteJoin(@Valid @RequestBody InviteJoinStudioRequest inviteJoinStudioRequest,HttpServletRequest request){
        Integer userId = JwtUtil.getId(request);
        service.inviteJoin(userId,inviteJoinStudioRequest);
        return new Response().success();
    }

    @GetMapping(value = "/show_studio_info/{studio_id}",produces = "application/json")
    public Response showStudioInfo(@PathVariable("studio_id")int studioId,HttpServletRequest request){
        Integer userId = JwtUtil.getId(request);
        StudioInfoResponse info = service.getStudioInfo(userId,studioId);
        return new Response().success(info);
    }

    @PostMapping(value = "/deal_apply",produces = "application/json")
    public Response dealApply(@Valid @RequestBody DealStudioApplyRequest dealStudioApplyRequest,HttpServletRequest request){
        Integer userId = JwtUtil.getId(request);
        service.dealApply(userId,dealStudioApplyRequest);
        return new Response().success();
    }

    @PostMapping(value = "/create_department",produces = "application/json")
    public Response createDepartment(@Valid @RequestBody CreateDepartmentRequest createDepartmentRequest,HttpServletRequest request){
        Integer userId = JwtUtil.getId(request);
        service.createDepartment(userId,createDepartmentRequest);
        return new Response().success();
    }

    @PostMapping(value = "/distribute_leader",produces = "application/json")
    public Response distributeLeader(@Valid @RequestBody DistributeLeaderRequest distributeLeaderRequest,HttpServletRequest request){
        Integer userId = JwtUtil.getId(request);
        service.distributeLeader(userId,distributeLeaderRequest);
        return new Response().success();
    }

    @GetMapping(value = "/list",produces = "application/json")
    public Response list(HttpServletRequest request){
        Integer userId = JwtUtil.getId(request);
        Map<String,Object> response = service.getStudioList(userId);
        return new Response().success(response);
    }

    @PostMapping(value = "/record_current",produces = "application/json")
    public Response recordCurrent(@RequestBody Integer studioId,HttpServletRequest request){
        Integer userId = JwtUtil.getId(request);
        service.recordCurrentStudio(studioId,userId);
        return new Response().success();
    }

}
