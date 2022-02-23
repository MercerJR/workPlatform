package com.project.workplatform.controller;

import com.project.workplatform.data.ValidConstant;
import com.project.workplatform.data.response.Response;
import com.project.workplatform.data.response.search.SearchGroupResponse;
import com.project.workplatform.data.response.search.SearchUserResponse;
import com.project.workplatform.service.SearchService;
import com.project.workplatform.util.JwtUtil;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * @Author: Mercer JR
 * @Date: 2022/2/18 18:30
 */
@RestController
@Validated
@RequestMapping("/search")
public class SearchController {

    @Autowired
    private SearchService service;

    @GetMapping(value = "/user", produces = "application/json")
    public Response searchUser(@NotBlank(message = ValidConstant.SEARCH_CONTENT_EMPTY)
                               @Size(max = 20, message = ValidConstant.SEARCH_CONTENT_LENGTH)
                               @RequestParam("search_content") String searchContent) {
        List<SearchUserResponse> list = service.getSearchUserResult(searchContent);
        return new Response().success(list);
    }

    @GetMapping(value = "/group", produces = "application/json")
    public Response searchGroup(@NotBlank(message = ValidConstant.SEARCH_CONTENT_EMPTY)
                                @Size(max = 20, message = ValidConstant.SEARCH_CONTENT_LENGTH)
                                @RequestParam("search_content") String searchContent) {
        List<SearchGroupResponse> list = service.getSearchGroupResult(searchContent);
        return new Response().success(list);
    }

}
