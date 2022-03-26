package com.project.workplatform.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WsMessageResponse {

    /**
     * 0：正常；1：普通错误；2：身份错误
     */
    private Integer code = 0;

    private Integer senderId;

    private String senderName;

    private String content;

    private String time;

}
