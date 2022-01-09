package com.project.workplatform.configration.interceptor;

import com.project.workplatform.dao.UserMapper;
import com.project.workplatform.data.Constant;
import com.project.workplatform.exception.CustomException;
import com.project.workplatform.exception.CustomExceptionType;
import com.project.workplatform.exception.ExceptionMessage;
import com.project.workplatform.pojo.User;
import com.project.workplatform.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Author: Mercer JR
 * @Date: 2022/1/8 14:46
 */
@Component
public class LoginInterceptor implements HandlerInterceptor {

    @Autowired
    private UserMapper mapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader(Constant.JWT_TOKEN);
        Integer id = JwtUtil.getId(token);
        User user = mapper.selectByPrimaryKey(id);
        boolean flag = JwtUtil.verifyToken(token, user.getPhoneNumber(), user.getPassword());
        if (!flag){
            throw new CustomException(CustomExceptionType.NORMAL_ERROR, ExceptionMessage.TOKEN_INVALID);
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
