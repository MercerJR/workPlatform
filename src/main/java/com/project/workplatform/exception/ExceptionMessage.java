package com.project.workplatform.exception;

/**
 * @Author: Mercer JR
 * @Date: 2022/1/8 10:38
 */
public interface ExceptionMessage {

    String NOT_LOGIN = "你尚未登陆，请登陆后再访问";
    String TOKEN_EXPIRE = "token已过期，请重新登陆";
    String TOKEN_INVALID = "token不合法";
    String TOKEN_NOT_EXIST = "token不存在";

    String ID_OR_PASSWORD_ERROR = "用户名或密码错误";
    String OLD_PASSWORD_WRONG = "旧的密码输入错误";
    String NEW_PASSWORD_AGAIN_WRONG = "两次输入的密码不相同";
    String PHONE_NUMBER_ALREADY_REGISTER = "该电话号码已被注册";

    String ALREADY_FRIEND = "你们已经是好友了";
    String ALREADY_DEAL_APPLY = "你已经处理过该请求了";

    String NOT_ADMIN = "你不是群聊的群主或管理员";
    String NOT_CREATOR = "你不是群主";
    String ALREADY_GROUP_MEMBER = "你已经是群聊成员了";
    String APPLY_ALREADY_DEAL = "该请求已被处理过了";
    String GROUP_NOT_EXIST = "该群聊不存在";
    String NOT_IN_GROUP = "你不是该群聊的成员";
    String USER_NOT_IN_GROUP = "该用户不是该群聊的成员";

    String PERMISSION_DENIED = "你没有权限处理该请求";

    String INVALID_INVITE_CODE = "无效的邀请码";
    String NOT_STUDIO_SUPER_ADMIN = "你不是工作室的超级管理员";
    String NOT_STUDIO_ADMIN = "你不是工作室的部门负责人";
    String NOT_STUDIO_CREATOR = "你不是工作室的创始人";
    String NOT_STUDIO_MEMBER = "你不是工作室的成员";
    String STUDIO_ID_INVALID = "无效的工作室请求";
    String USER_NOT_IN_STUDIO = "该用户不是工作室成员";
    String USER_ALREADY_SUPER_ADMIN = "该用户已经是超级管理员了";
    String USER_ALREADY_ADMIN = "该用户已经是管理员了";
    String USER_NOT_IN_THAT_DEPARTMENT = "该用户不是该工作室的成员，请输入正确的用户名称和工作室名称";
    String USER_NOT_EXIST = "该用户不存在，请输入正确的名称或手机号";
    String PARENT_DEPARTMENT_NAME_WRONG = "请输入正确的上级部门名称";
    String DEPARTMENT_ALREADY_EXIST = "该部门已经存在，无需重复创建";
    String DEPARTMENT_NAME_WRONG = "请输入正确的部门名称";
    String CAN_NOT_DEAL_YOURSELF = "不能对自己进行操作";
    String PARENT_DEPARTMENT_DO_NOT_BELONG_STUDIO = "该上级部门不属于本工作室";
    String CAN_NOT_DEAL_TOP_DEPARTMENT = "不能删除顶级部门";

}
