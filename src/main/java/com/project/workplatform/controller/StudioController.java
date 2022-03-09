package com.project.workplatform.controller;

import com.project.workplatform.data.request.studio.*;
import com.project.workplatform.data.response.Response;
import com.project.workplatform.data.response.studio.*;
import com.project.workplatform.pojo.StudioContactInfo;
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

    @GetMapping(value = "/show_studio_base_info/{studio_id}",produces = "application/json")
    public Response showStudioBaseInfo(@PathVariable("studio_id") Integer studioId,HttpServletRequest request){
        Integer userId = JwtUtil.getId(request);
        StudioBaseInfoResponse response = service.getStudioBaseInfo(studioId,userId);
        return new Response().success(response);
    }

    @GetMapping(value = "/show_people_info/{studio_id}",produces = "application/json")
    public Response showPeopleInfo(@PathVariable("studio_id") Integer studioId){
        StudioPeopleInfoResponse response = service.getStudioPeopleInfo(studioId);
        return new Response().success(response);
    }

    @GetMapping(value = "/show_studio_contact_info/{studio_id}",produces = "application/json")
    public Response showStudioContactInfo(@PathVariable("studio_id") Integer studioId){
        StudioContactInfoResponse response = service.getStudioContactInfo(studioId);
        return new Response().success(response);
    }

    @PostMapping(value = "/update_studio_info",produces = "application/json")
    public Response updateStudioInfo(@Valid @RequestBody UpdateStudioInfoRequest updateStudioInfoRequest,
                                     HttpServletRequest request){
        Integer userId = JwtUtil.getId(request);
        service.updateStudioInfo(updateStudioInfoRequest,userId);
        return new Response().success();
    }

    @PostMapping(value = "/update_studio_contact_info",produces = "application/json")
    public Response updateStudioContactInfo(@Valid @RequestBody UpdateStudioContactInfoRequest updateStudioContactInfoRequest,
                                            HttpServletRequest request){
        Integer userId = JwtUtil.getId(request);
        service.updateStudioContactInfo(updateStudioContactInfoRequest,userId);
        return new Response().success();
    }

    @GetMapping(value = "/show_admin_list",produces = "application/json")
    public Response showAdminList(@RequestParam("studio_id") Integer studioId,
                                       @RequestParam("type") String type,
                                       @RequestParam("search_content") String searchContent,
                                       @RequestParam(value = "select",required = false,defaultValue = "user") String select,
                                       HttpServletRequest request){
        Integer userId = JwtUtil.getId(request);
        List<StudioAdminResponse> response = service.getStudioAdminResponse(studioId,userId,type,searchContent,select);
        return new Response().success(response);
    }

    @PostMapping(value = "/update_admin",produces = "application/json")
    public Response updateAdmin(@RequestBody UpdateStudioRoleRequest updateStudioRoleRequest,HttpServletRequest request){
        Integer userId = JwtUtil.getId(request);
        service.updateStudioRole(updateStudioRoleRequest,userId);
        return new Response().success();
    }

}
