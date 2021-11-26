package com.taewon.shoppingmall.util;

import com.taewon.shoppingmall.item.BoardItem;

import java.util.Comparator;

public class BoardStarCountComparator implements Comparator<BoardItem> {
    @Override
    public int compare(BoardItem item, BoardItem t1) {
        if(item.getStarCount() > t1.getStarCount()){
            return -1;
        }
        else if(item.getStarCount() < t1.getStarCount()){
            return 1;
        }
        return 0;
    }
}
