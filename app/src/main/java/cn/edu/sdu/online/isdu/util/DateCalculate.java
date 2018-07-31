package cn.edu.sdu.online.isdu.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 *
 * @author TYZ
 * @date 2018/7/19
 */

public class DateCalculate {

    private static long  nowMill = EnvVariables.firstWeekTimeMillis;
    private static String date;

    public static String Cal(long week, long day){
        date = "";
        nowMill = EnvVariables.firstWeekTimeMillis;
        nowMill += (week - 1) * 7 * 24 * 60 * 60 * 1000;
        nowMill += (day - 1) * 24 * 60 * 60 * 1000;
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(nowMill);
        date += calendar.get(Calendar.YEAR);
        date += "-" + (calendar.get(Calendar.MONTH) + 1);
        date += "-" + calendar.get(Calendar.DAY_OF_MONTH);
        return date;
    }

    public static String getExpressionDate(long timeMillis) {
        return (System.currentTimeMillis() - timeMillis < 60 * 1000) ?
            "刚刚" : ((System.currentTimeMillis() - timeMillis < 24 * 60 * 60 * 1000) ?
            (System.currentTimeMillis() - timeMillis) / (60 * 60 * 1000) + " 小时前" : (
         (System.currentTimeMillis() - timeMillis < 48 * 60 * 60 * 1000) ?
                 "昨天 " + new SimpleDateFormat("HH:mm").format(timeMillis)
                    : new SimpleDateFormat("yyyy-MM-dd HH:mm").format(timeMillis)));
    }

}
