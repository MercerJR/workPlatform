package com.project.workplatform.controller;

import com.project.workplatform.data.request.user.ChangePasswordRequest;
import com.project.workplatform.data.request.user.UserInfoRequest;
import com.project.workplatform.data.request.user.UserLoginRequest;
import com.project.workplatform.data.response.Response;
import com.project.workplatform.data.response.user.LoginResponse;
import com.project.workplatform.pojo.UserInfo;
import com.project.workplatform.service.UserService;
import com.project.workplatform.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * @Author: Mercer JR
 * @Date: 2022/1/8 10:42
 */
@RestController
@Validated
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService service;

    @PostMapping(value = "/register",produces = "application/json")
    public Response register(@Valid @RequestBody UserLoginRequest request){
        service.register(request);
        return new Response().success();
    }

    @PostMapping(value = "/login",produces = "application/json")
    public Response login(@Valid @RequestBody UserLoginRequest userLoginRequest){
        LoginResponse response = service.loginUser(userLoginRequest.getPhoneNumber(),userLoginRequest.getPassword());
        return new Response().success(response);
    }

    @PostMapping(value = "/update_info",produces = "application/json")
    public Response updateInfo(@Valid @RequestBody UserInfoRequest userInfoRequest, HttpServletRequest request){
        Integer userId = JwtUtil.getId(request);
        service.updateUserInfo(userId,userInfoRequest);
        return new Response().success();
    }

    @GetMapping(value = "/show_info",produces = "application/json")
    public Response showInfo(HttpServletRequest request){
        Integer userId = JwtUtil.getId(request);
        UserInfo userInfo = service.getUserInfo(userId);
        return new Response().success(userInfo);
    }

    @PostMapping(value = "/change_password",produces = "application/json")
    public Response changePassword(@RequestBody ChangePasswordRequest changePasswordRequest, HttpServletRequest request){
        Integer userId = JwtUtil.getId(request);
        service.changePassword(changePasswordRequest,userId);
        return new Response().success();
    }

}
