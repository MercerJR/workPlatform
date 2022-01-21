package com.project.workplatform.util;

import java.util.Random;

/**
 * @Author: Mercer JR
 * @Date: 2022/1/21 10:47
 */
public class RandomUtil {

    private static final String BASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    public static String randomString(int length){
        char[] ch = BASE.toCharArray();
        Random random = new Random();
        StringBuilder builder = new StringBuilder();
        for (int i = 0;i < length;i++){
            builder.append(ch[random.nextInt(ch.length)]);
        }
        return builder.toString();
    }

    public static void main(String[] args) {
        System.out.println(randomString(8));
    }

}
