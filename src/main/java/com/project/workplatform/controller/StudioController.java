package com.project.workplatform.controller;

import com.project.workplatform.data.request.studio.*;
import com.project.workplatform.data.response.Response;
import com.project.workplatform.data.response.studio.StudioInfoResponse;
import com.project.workplatform.pojo.Studio;
import com.project.workplatform.service.StudioService;
import com.project.workplatform.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

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

}
