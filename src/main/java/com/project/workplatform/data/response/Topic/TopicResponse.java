package com.project.workplatform.data.response.Topic;

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

    private String createTime;

    private boolean like;

}
