package com.project.workplatform.service;

import com.project.workplatform.dao.UserInfoMapper;
import com.project.workplatform.dao.UserMapper;
import com.project.workplatform.data.request.ChangePasswordRequest;
import com.project.workplatform.data.request.UserInfoRequest;
import com.project.workplatform.data.request.UserLoginRequest;
import com.project.workplatform.exception.CustomException;
import com.project.workplatform.exception.CustomExceptionType;
import com.project.workplatform.exception.ExceptionMessage;
import com.project.workplatform.pojo.User;
import com.project.workplatform.pojo.UserInfo;
import com.project.workplatform.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @Author: Mercer JR
 * @Date: 2022/1/8 10:53
 */
@Service
@Transactional(rollbackFor = RuntimeException.class)
public class UserService {

    @Autowired
    private UserMapper mapper;

    @Autowired
    private UserInfoMapper infoMapper;

    public void register(UserLoginRequest request) {
        if (mapper.selectByPhone(request.getPhoneNumber()) != null){
            throw new CustomException(CustomExceptionType.NORMAL_ERROR,ExceptionMessage.PHONE_NUMBER_ALREADY_REGISTER);
        }
        mapper.insert(new User(request.getPhoneNumber(),request.getPassword()));
    }

    public String loginUser(String phoneNumber, String password) {
        User user = mapper.selectByPhoneAndPass(phoneNumber, password);
        if (user == null) {
            throw new CustomException(CustomExceptionType.NORMAL_ERROR, ExceptionMessage.ID_OR_PASSWORD_ERROR);
        }
        return JwtUtil.createToken(user);
    }

    public void updateUserInfo(UserInfoRequest request) {
        UserInfo info = infoMapper.selectByUser(request.getUserId());
        info.setUserId(request.getUserId());
        info.setDescribe(request.getDescribe());
        info.setGender(request.getGender());
        info.setHobby(request.getHobby());
        info.setLivePlace(request.getLivePlace());
        info.setHometown(request.getHometown());
        infoMapper.updateByPrimaryKeySelective(info);
    }

    public UserInfo getUserInfo(Integer userId) {
        return infoMapper.selectByUser(userId);
    }

    public void changePassword(ChangePasswordRequest request, Integer id) {
        if (!request.getNewPassword().equals(request.getNewPasswordAgain())) {
            throw new CustomException(CustomExceptionType.NORMAL_ERROR, ExceptionMessage.NEW_PASSWORD_AGAIN_WRONG);
        }
        if (mapper.checkPasswordCorrect(id, request.getOldPassword()) == 0) {
            throw new CustomException(CustomExceptionType.NORMAL_ERROR, ExceptionMessage.OLD_PASSWORD_WRONG);
        }
        mapper.updatePasswordByPrimaryKey(id, request.getNewPassword());
    }
}
