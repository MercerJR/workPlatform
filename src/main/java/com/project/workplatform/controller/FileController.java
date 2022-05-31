package com.project.workplatform.controller;

import com.project.workplatform.data.request.file.DealFileApplyRequest;
import com.project.workplatform.data.request.file.FileApplyRequest;
import com.project.workplatform.data.request.file.UploadFileRequest;
import com.project.workplatform.data.response.Response;
import com.project.workplatform.service.FileService;
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
import javax.servlet.http.HttpServletResponse;

/**
 * @author zengjingran
 */
@RestController
@Validated
@RequestMapping("/file")
public class FileController {

    @Autowired
    private FileService service;

    @PostMapping(value = "/upload",produces = "application/json")
    public Response upload(@RequestBody UploadFileRequest fileRequest, HttpServletRequest request){
        Integer userId = JwtUtil.getId(request);
        service.saveFile(fileRequest.getFile(),userId);
        return new Response().success();
    }

    @PostMapping(value = "/apply",produces = "application/json")
    public Response apply(@RequestBody FileApplyRequest fileApplyRequest, HttpServletRequest request){
        Integer userId = JwtUtil.getId(request);
        service.apply(fileApplyRequest,userId);
        return new Response().success();
    }

    @PostMapping(value = "/deal_apply",produces = "application/json")
    public Response dealApply(@RequestBody DealFileApplyRequest dealFileApplyRequest, HttpServletRequest request){
        Integer userId = JwtUtil.getId(request);
        service.dealApply(dealFileApplyRequest,userId);
        return new Response().success();
    }

    @GetMapping(value = "/download/{file_id}",produces = "application/json")
    public Response download(@PathVariable("file_id") Integer fileId, HttpServletRequest request, HttpServletResponse response){
        Integer userId = JwtUtil.getId(request);
        service.download(fileId,userId,response);
        return new Response().success();
    }

}
