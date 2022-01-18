package com.project.workplatform.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Author: Mercer JR
 * @Date: 2022/1/16 19:23
 */
public class DateFormatUtil {
    
    public static final String DAY_FORMAT = "yyyy-MM-dd";

    public static final String MINUTE_FORMAT = "yyyy-MM-dd HH:mm";

    public static final String SECOND_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public static String getStringDateByDate(Date date,String format){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        return simpleDateFormat.format(date);
    }

}
