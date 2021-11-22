package com.taewon.shoppingmall.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.taewon.shoppingmall.item.AdsItem;

import java.util.ArrayList;

public class AdsFlipperAdapter extends BaseAdapter {
    Context context;
    ArrayList<AdsItem> items;
    public AdsFlipperAdapter(Context context, ArrayList<AdsItem> items){
        this.context = context;
        this.items = items;
    }
    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public AdsItem getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return null;
    }
}
