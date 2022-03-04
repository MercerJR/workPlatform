package com.project.workplatform.util;

import org.springframework.util.StringUtils;

import java.util.regex.Pattern;

/**
 * @Author: Mercer JR
 * @Date: 2022/1/12 19:48
 */
public class ValidUtil {

    final static Pattern PHONE_NUMBER_PATTERN = Pattern.compile("^1[345678]\\d{9}$");
    final static Pattern MAIL_PATTERN = Pattern.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");

    public static boolean checkPhoneNumber(String phoneNumber) {
        return phoneNumber != null && !"".equals(phoneNumber) &&
                PHONE_NUMBER_PATTERN.matcher(phoneNumber).matches();
    }

    public static boolean checkMail(String mail) {
        if (mail == null || "".equals(mail)) {
            return true;
        }
        return MAIL_PATTERN.matcher(mail).matches();
    }
}
