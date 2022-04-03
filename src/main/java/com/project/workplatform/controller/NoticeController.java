package com.project.workplatform.controller;

import com.project.workplatform.data.response.Response;
import com.project.workplatform.data.response.notice.NoticeResponse;
import com.project.workplatform.service.NoticeService;
import com.project.workplatform.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author zengjingran
 */
@RestController
@Validated
@RequestMapping("/notice")
public class NoticeController {

    @Autowired
    private NoticeService service;

    @GetMapping(value = "/show_notice_list",produces = "application/json")
    public Response showNoticeList(HttpServletRequest request){
        Integer userId = JwtUtil.getId(request);
        List<NoticeResponse> response = service.getNoticeList(userId);
        return new Response().success(response);
    }

}
