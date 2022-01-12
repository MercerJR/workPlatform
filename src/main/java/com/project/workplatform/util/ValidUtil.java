package com.project.workplatform.util;

import org.springframework.util.StringUtils;

import java.util.regex.Pattern;

/**
 * @Author: Mercer JR
 * @Date: 2022/1/12 19:48
 */
public class ValidUtil {

    final static Pattern PATTERN = Pattern.compile("^1[345678]\\d{9}$");

    public static boolean checkPhoneNumber(String phoneNumber){
        return phoneNumber != null && !"".equals(phoneNumber) && PATTERN.matcher(phoneNumber).matches();
    }

}
