package com.microservice.util;

import java.util.Calendar;

public class TimeUtil {

    public static String getCurrentTimeString(){
        Calendar now = Calendar.getInstance();
        int hour = now.get(Calendar.HOUR_OF_DAY);
        int minute = now.get(Calendar.MINUTE);
        int second = now.get(Calendar.SECOND);
        int millis = now.get(Calendar.MILLISECOND);

        return String.format("%02d:%02d:%02d:%03d", hour, minute, second, millis);
    }
}
