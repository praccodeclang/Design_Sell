package com.taewon.shoppingmall.util;

import com.taewon.shoppingmall.item.BoardItem;

import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;

public class BoardDateComparator implements Comparator<BoardItem> {
    boolean isDesc;
    public BoardDateComparator(){
        isDesc = false;
    }
    public BoardDateComparator(boolean isDesc){
        this.isDesc = isDesc;
    }
    @Override
    public int compare(BoardItem item, BoardItem t1) {
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
