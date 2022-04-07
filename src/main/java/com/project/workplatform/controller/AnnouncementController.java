package com.project.workplatform.controller;

import com.project.workplatform.data.request.announcement.PublishAnnouncementRequest;
import com.project.workplatform.data.response.AnnouncementResponse;
import com.project.workplatform.data.response.Response;
import com.project.workplatform.service.AnnouncementService;
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
import javax.validation.Valid;
import java.util.List;

/**
 * @author zengjingran
 */
@RestController
@Validated
@RequestMapping("/announcement")
public class AnnouncementController {

    @Autowired
    private AnnouncementService service;

    @PostMapping(value = "/publish",produces = "application/json")
    public Response publish(@Valid @RequestBody PublishAnnouncementRequest publishAnnouncementRequest, HttpServletRequest request){
        Integer userId = JwtUtil.getId(request);
        service.publish(publishAnnouncementRequest,userId);
        return new Response().success();
    }

    @GetMapping(value = "/show_announcement_list/{studio_id}",produces = "application/json")
    public Response showAnnouncementList(@PathVariable("studio_id") Integer studioId){
        List<AnnouncementResponse> responses = service.getAnnouncementList(studioId);
        return new Response().success(responses);
    }

    @GetMapping(value = "/show_one_announcement/{announcement_id}",produces = "application/json")
    public Response showOneAnnouncement(@PathVariable("announcement_id") Integer announcementId){
        AnnouncementResponse responses = service.getOneAnnouncement(announcementId);
        return new Response().success(responses);
    }

}
