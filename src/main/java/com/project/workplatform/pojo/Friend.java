package com.project.workplatform.pojo;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: Mercer JR
 * @Date: 2022/1/9 14:45
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Friend implements Serializable {
    private Integer id;

    private Integer userId;

    private Integer friendId;

    private static final long serialVersionUID = 1L;

    public Friend(int userId,int friendId){
        this.userId = userId;
        this.friendId = friendId;
    }
}