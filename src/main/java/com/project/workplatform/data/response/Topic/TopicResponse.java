package com.project.workplatform.data.response.Topic;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Author: Mercer JR
 * @Date: 2022/1/18 11:51
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TopicResponse {

    private int topicId;

    private int publisherId;

    private String title;

    private String content;

    private List<String> imgList;

    private List<String> labelList;

    private int likeNum;

    private int commentNum;

    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    private String createTime;

    private boolean like;

}
