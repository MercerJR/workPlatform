package com.project.workplatform.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Author: Mercer JR
 * @Date: 2022/1/16 19:23
 */
public class DateFormatUtil {

    public static String getStringDateByDate(Date date){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return simpleDateFormat.format(date);
    }

}
