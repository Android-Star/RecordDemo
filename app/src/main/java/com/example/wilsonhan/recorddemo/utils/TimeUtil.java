package com.example.wilsonhan.recorddemo.utils;

import java.util.Date;

/**
 * Created by wilsonhan on 2018/2/8.
 */

public class TimeUtil {
    /**
     * 将毫秒转换成秒
     *
     * @param time
     * @return
     */
    public static int convertToSecond(Long time) {
        Date date = new Date();
        date.setTime(time);
        return date.getSeconds();
    }

    /**
     * @param 要转换的毫秒数
     * @return 该毫秒数转换为 * days * hours * minutes * seconds 后的格式
     * @author fy.zhang
     */
    public static String formatDuring(long mss) {
        long days = mss / (1000 * 60 * 60 * 24);
        long hours = (mss % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
        long minutes = (mss % (1000 * 60 * 60)) / (1000 * 60);
        long seconds = (mss % (1000 * 60)) / 1000;
        if (days == 0) {
            if (hours == 0) {
                if (minutes == 0) {
                    if (seconds == 0) {
                        return "";
                    } else {
                        return seconds + "\'\'";
                    }
                } else {
                    return minutes + "\'"
                            + seconds + "\'\'";
                }
            } else {
                return hours + ":" + minutes + "\'"
                        + seconds + "\'\'";
            }
        } else {
            return days + " 天 " + hours + ":" + minutes + "\'"
                    + seconds + "\'\'";
        }
    }
}
