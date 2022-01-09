package com.project.workplatform.exception;

/**
 * @Author: Mercer JR
 * @Date: 2022/1/8 10:38
 */
public interface ExceptionMessage {

    String NOT_LOGIN = "你尚未登陆，请登陆后再访问";
    String TOKEN_EXPIRE = "token已过期，请重新登陆";
    String TOKEN_INVALID = "token不合法";

    String ID_OR_PASSWORD_ERROR = "用户名或密码错误";
    String OLD_PASSWORD_WRONG = "旧的密码输入错误";
    String NEW_PASSWORD_AGAIN_WRONG = "两次输入的密码不相同";
    String PHONE_NUMBER_ALREADY_REGISTER = "该电话号码已被注册";

}
