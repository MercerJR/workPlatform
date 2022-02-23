package com.project.workplatform.data;

/**
 * @Author: Mercer JR
 * @Date: 2022/1/8 10:45
 */
public interface ValidConstant {

    //用户模块 校验常量

    String PHONE_NUMBER_EMPTY = "电话号码不能位空";
    String PHONE_NUMBER_LENGTH = "电话号码长度不对";
    String PASSWORD_EMPTY = "密码不能为空";
    String PASSWORD_LENGTH = "密码长度必须在8位及以上";
    String NAME_EMPTY = "姓名不能为空";
    String PHONE_NUMBER_WRONG_FORMAT = "请输入正确的11位手机号格式";
    String NEW_PASSWORD_EMPTY = "新密码不能为空";


    //好友模块 校验常量

    String APPLY_MESSAGE_LENGTH = "申请消息不能超过50字符";


    //群聊模块 校验常量

    String GROUP_NAME_EMPTY = "群聊名称不能为空";
    String GROUP_NAME_LENGTH = "群聊名称不得超过20个字符";
    String GROUP_CLASSIFY_LENGTH = "群聊分类不得超过20个字符";


    //话题圈模块 校验常量

    String TITLE_LENGTH = "标题不得超过20个字符";
    String COMMENT_EMPTY = "评论内容不能为空";
    String CONTENT_EMPTY = "内容不能为空";


    //工作室 校验常量

    String STUDIO_NAME_EMPTY = "工作室名称不能为空";
    String STUDIO_NAME_LENGTH = "工作室名称不能超过20个字符";
    String INVALID_CODE_EMPTY = "请输入邀请码";
    String INVALID_CODE_LENGTH = "请输入有效的8位邀请码";
    String STUDIO_APPLY_MESSAGE_LENGTH = "申请消息不能超过50个字符";

    //搜索模块 校验常量

    String SEARCH_CONTENT_EMPTY = "搜索内容不能为空";
    String SEARCH_CONTENT_LENGTH = "搜索内容长度不能超过20个字符";

}
