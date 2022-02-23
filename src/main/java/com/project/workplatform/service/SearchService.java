package com.project.workplatform.service;

import com.project.workplatform.dao.GroupMapper;
import com.project.workplatform.dao.UserMapper;
import com.project.workplatform.data.request.search.SearchGroupRequest;
import com.project.workplatform.data.request.search.SearchUserRequest;
import com.project.workplatform.data.response.search.SearchGroupResponse;
import com.project.workplatform.data.response.search.SearchUserResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.regex.Pattern;

/**
 * @Author: Mercer JR
 * @Date: 2022/2/18 18:47
 */
@Service
@Transactional(rollbackFor = RuntimeException.class)
public class SearchService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private GroupMapper groupMapper;

    public List<SearchUserResponse> getSearchUserResult(String searchContent) {
        //TODO 判断搜索内容是否是手机号，如果是则不再按用户名搜索，如果不是则按用户名进行搜索
        return checkPhoneNumberFormat(searchContent) ?
                userMapper.selectUserInfoByPhone(searchContent) :
                userMapper.selectUserInfoByNameFuzzy(searchContent);
    }

    public List<SearchGroupResponse> getSearchGroupResult(String searchContent) {
        return groupMapper.selectGroupInfoByGroupNameFuzzy(searchContent);
    }

    private boolean checkPhoneNumberFormat(String str){
        return Pattern.matches("^1[3-9]\\d{9}$", str);
    }

}
