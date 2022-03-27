package com.project.workplatform.controller;

import com.project.workplatform.data.request.chatInfo.UpdateChatListRequest;
import com.project.workplatform.data.response.Response;
import com.project.workplatform.data.response.chatInfo.ChatListResponse;
import com.project.workplatform.service.ChatInfoService;
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
import java.util.List;

@RestController
@Validated
@RequestMapping("/chat_info")
public class ChatInfoController {

    @Autowired
    private ChatInfoService service;

    @PostMapping(value = "/update_chat_list",produces = "application/json")
    public Response updateChatList(@RequestBody UpdateChatListRequest updateChatListRequest, HttpServletRequest request){
        Integer userId = JwtUtil.getId(request);
        service.updateChatList(updateChatListRequest,userId);
        return new Response().success();
    }

    @GetMapping(value = "/show_chat_list/{studio_id}",produces = "application/json")
    public Response showChatList(@PathVariable("studio_id") Integer studioId, HttpServletRequest request){
        Integer userId = JwtUtil.getId(request);
        List<ChatListResponse> response = service.getChatList(studioId,userId);
        return new Response().success(response);
    }

}
