package com.project.workplatform.data.response.notice;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zengjingran
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class NoticeResponse {

    private String noticeTitle;

    private String noticeContent;

    private Integer noticeType;

    private boolean needDeal;

    private Long createTime;

}
