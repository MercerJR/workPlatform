package com.project.workplatform.pojo;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: Mercer JR
 * @Date: 2022/1/8 10:34
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User implements Serializable {
    private Integer id;

    private String phoneNumber;

    private String password;

    private static final long serialVersionUID = 1L;

    public User(String phoneNumber,String password){
        this.phoneNumber = phoneNumber;
        this.password = password;
    }
}