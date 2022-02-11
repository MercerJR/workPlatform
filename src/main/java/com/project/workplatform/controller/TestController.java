package com.project.workplatform.controller;

import com.project.workplatform.data.request.test.TestRequest;
import com.project.workplatform.data.response.Response;
import org.springframework.web.bind.annotation.*;

/**
 * @Author: Mercer JR
 * @Date: 2022/1/28 12:41
 */
@RestController
@RequestMapping("/test")
//@CrossOrigin(origins = "*")
public class TestController {

    @GetMapping(value = "/get/{num}",produces = "application/json")
    public Response get(@PathVariable int num){
        return new Response().success(num);
    }

    @PostMapping(value = "/post",produces = "application/json")
    public Response post(@RequestBody TestRequest testRequest){
        return new Response().success(testRequest);
    }

    @GetMapping(value = "/get_club",produces = "application/json")
    public Response getClub(@RequestParam("club") String club){
        String data = club + "冲！";
        return new Response().success(data);
    }

}
