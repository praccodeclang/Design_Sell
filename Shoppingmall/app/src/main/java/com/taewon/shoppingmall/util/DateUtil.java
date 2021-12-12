package com.taewon.shoppingmall.util;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {

    public static String calUploadDate(String date1, String date2){
        Date format1;
        Date format2;
        try{
            format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(date1);
            format2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(date2);
            long diffSec = (format1.getTime() - format2.getTime()) / 1000; //초 차이

            long diffDays = diffSec / (24*60*60); // 일 수 차이
            if(diffDays > 0){
                return diffDays +"일 전";
            }

            long diffHour = (format1.getTime() - format2.getTime()) / 3600000; //시간 차이
            if(diffHour > 0){
                return diffHour + "시간 전";
            }

            long diffMin = (format1.getTime() - format2.getTime()) / 60000; //분 차이
            return diffMin+"분 전";
        }catch (Exception e){
            e.printStackTrace();
            Log.e("시간 에러", e.getMessage());
        }
        return "";
    }
}
