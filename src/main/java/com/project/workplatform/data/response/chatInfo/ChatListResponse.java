package com.project.workplatform.data.response.chatInfo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatListResponse {

    private Integer chatId;

    private String chatName;

    /**
     * 0：单人聊天；1：群聊；
     */
    private Integer targetType;

    private String icon;

    /**
     * 0：外部消息；1：内部消息
     */
    private Integer insideType;

    /**
     * insideType的描述
     */
    private String insideTag;

}
