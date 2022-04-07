package com.project.workplatform.data.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zengjingran
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AnnouncementResponse {

    private Integer announcementId;

    private String title;

    private String content;

    private String publisherId;

    private String publisherName;

    private String time;

}
