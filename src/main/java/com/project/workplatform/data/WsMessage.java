package com.project.workplatform.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WsMessage {

    /**
     * 0：授权；1：文字消息
     */
    private Integer type;

    private String content;

    private Integer targetId;

    private Integer targetType;

    private Integer studioId;

}
