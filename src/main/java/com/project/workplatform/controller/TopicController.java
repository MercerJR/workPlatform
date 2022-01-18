package com.project.workplatform.controller;

import com.project.workplatform.data.request.topic.PublishCommentRequest;
import com.project.workplatform.data.request.topic.PublishTopicRequest;
import com.project.workplatform.data.response.Response;
import com.project.workplatform.data.response.Topic.CommentResponse;
import com.project.workplatform.data.response.Topic.TopicResponse;
import com.project.workplatform.service.TopicService;
import com.project.workplatform.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

/**
 * @Author: Mercer JR
 * @Date: 2022/1/18 10:15
 */
@RestController
@Validated
@RequestMapping("/topic")
public class TopicController {

    @Autowired
    private TopicService service;

    @PostMapping(value = "/publish",produces = "application/json")
    public Response publish(@Valid @RequestBody PublishTopicRequest publishTopicRequest, HttpServletRequest request){
        Integer userId = JwtUtil.getId(request);
        service.publicTopic(userId,publishTopicRequest);
        return new Response().success();
    }

    @GetMapping(value = "/show_topic_list/{studio_id}",produces = "application/json")
    public Response showTopicList(@PathVariable("studio_id") int studioId,HttpServletRequest request){
        Integer userId = JwtUtil.getId(request);
        List<TopicResponse> list = service.getTopicList(userId,studioId);
        return new Response().success(list);
    }

    @PostMapping(value = "/like",produces = "application/json")
    public Response like(@RequestBody int topicId,HttpServletRequest request){
        Integer userId = JwtUtil.getId(request);
        service.like(userId,topicId);
        return new Response().success();
    }

    @PostMapping(value = "/comment",produces = "application/json")
    public Response comment(@Valid @RequestBody PublishCommentRequest publishCommentRequest,HttpServletRequest request){
        Integer userId = JwtUtil.getId(request);
        service.comment(userId,publishCommentRequest);
        return new Response().success();
    }

    @GetMapping(value = "/show_comment_list/{topic_id}",produces = "application/json")
    public Response showCommentList(@PathVariable("topic_id")int topicId){
        List<CommentResponse> list = service.getCommentList(topicId);
        return new Response().success(list);
    }

}
