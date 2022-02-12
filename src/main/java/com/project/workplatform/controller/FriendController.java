package com.project.workplatform.controller;

import com.project.workplatform.data.request.friend.ApplyFriendRequest;
import com.project.workplatform.data.request.friend.DealApplyRequest;
import com.project.workplatform.data.request.friend.DeleteFriendRequest;
import com.project.workplatform.data.response.Response;
import com.project.workplatform.data.response.friend.FriendInfoResponse;
import com.project.workplatform.data.response.friend.FriendListResponse;
import com.project.workplatform.service.FriendService;
import com.project.workplatform.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @Author: Mercer JR
 * @Date: 2022/1/9 14:43
 */
@RestController
@Validated
@RequestMapping("/friend")
public class FriendController {

    @Autowired
    private FriendService service;

    @PostMapping(value = "/apply", produces = "application/json")
    public Response apply(@RequestBody ApplyFriendRequest applyFriendRequest, HttpServletRequest request) {
        Integer userId = JwtUtil.getId(request);
        service.applyFriend(userId, applyFriendRequest);
        return new Response().success();
    }

    @PostMapping(value = "/deal_apply", produces = "application/json")
    public Response dealApply(@RequestBody DealApplyRequest dealApplyRequest, HttpServletRequest request) {
        Integer userId = JwtUtil.getId(request);
        service.dealApply(userId, dealApplyRequest);
        return new Response().success();
    }

    @PostMapping(value = "/delete", produces = "application/json")
    public Response delete(@RequestBody DeleteFriendRequest deleteFriendRequest, HttpServletRequest request) {
        Integer userId = JwtUtil.getId(request);
        service.delete(userId, deleteFriendRequest);
        return new Response().success();
    }

    @GetMapping(value = "/list", produces = "application/json")
    public Response list(HttpServletRequest request) {
        Integer userId = JwtUtil.getId(request);
        List<FriendListResponse> friendList = service.getList(userId);
        return new Response().success(friendList);
    }

    @GetMapping(value = "/show_friend_info/{friend_id}", produces = "application/json")
    public Response showOne(@PathVariable("friend_id") int friendId, HttpServletRequest request) {
        FriendInfoResponse friendInfoResponse = service.getFriendInfo(friendId);
        return new Response().success(friendInfoResponse);
    }

}
