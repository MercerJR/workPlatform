package com.project.workplatform.data.request.chatInfo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateChatListRequest {

    private Integer chatId;

    /**
     * 0：单人聊天；1：群聊；
     */
    private Integer targetType;

}
