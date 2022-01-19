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

}
