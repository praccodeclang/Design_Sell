package com.taewon.shoppingmall.util;

import com.taewon.shoppingmall.item.BoardItem;
import com.taewon.shoppingmall.item.NotifyItem;

import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;

public class NotifyDateComparator implements Comparator<NotifyItem> {
    boolean isDesc;
    public NotifyDateComparator(){
        isDesc = false;
    }
    public NotifyDateComparator(boolean isDesc){
        this.isDesc = isDesc;
    }
    @Override
    public int compare(NotifyItem item, NotifyItem t1) {
        Date date1 = null;
        Date date2 = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            date1 = sdf.parse(item.getDateString());
            date2 = sdf.parse(t1.getDateString());
        }catch (Exception e){
            e.printStackTrace();
        }

        if(!isDesc){
            return date2.compareTo(date1);
        }
        else {
            return date1.compareTo(date2);
        }
    }
}
