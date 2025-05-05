package com.bjpowernode.crm.commons.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *  对 Date 类型的数据进行处理的工具
 */
public class DateUtils {

    /**
     * 对指定的 Date 对象进行格式化 yyyy-MM-dd HH:mm:ss
     * @param date
     * @return
     */
    public static String formateDateTime(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateStr = sdf.format(date);
        return dateStr;
    }
}
