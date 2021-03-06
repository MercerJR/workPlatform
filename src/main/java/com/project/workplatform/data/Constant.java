package com.project.workplatform.data;

/**
 * @Author: Mercer JR
 * @Date: 2022/1/8 10:35
 */
public interface Constant {

    String JWT_ISSUER = "MercerJR";
    String JWT_CLAIM_ID = "id";
    String JWT_TOKEN = "token";

    String INNER_GROUP = "工作室内部群聊";
    String OUTSIDE_GROUP = "外部群聊";

    String REDIS_LIKE_KEY_PREFIX = "like:";
    String REDIS_INVITE_CODE_KEY_PREFIX = "inviteCode:";
    String REDIS_CURRENT_STUDIO_KEY_PREFIX = "currentStudio:";
    String REDIS_NOT_READ_MSG_SENDER_KEY_PREFIX = "notReadMsgSender:";
    String REDIS_CHAT_LIST_KEY_PREFIX = "chatListKey:";

    Integer INVITE_CODE_EXPIRE_DAY = 40;

}
