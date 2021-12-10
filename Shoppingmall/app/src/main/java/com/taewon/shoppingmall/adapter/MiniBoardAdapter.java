package com.taewon.shoppingmall.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.taewon.shoppingmall.R;
import com.taewon.shoppingmall.item.BoardItem;

import java.util.ArrayList;

public class MiniBoardAdapter extends BaseAdapter {
    Context context;
    ArrayList<BoardItem> items;

    public MiniBoardAdapter(Context context, ArrayList<BoardItem> items){
        this.context = context;
        this.items = items;
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return null;
    }
}
