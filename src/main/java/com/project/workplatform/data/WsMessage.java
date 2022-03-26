package com.project.workplatform.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WsMessage {

    private Integer type;

    private String content;

    private Integer targetId;

    private Integer targetType;

}
