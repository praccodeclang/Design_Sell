package com.taewon.shoppingmall.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.taewon.shoppingmall.R;
import com.taewon.shoppingmall.activity.MainActivity2;
import com.taewon.shoppingmall.activity.SearchActivity;

import java.util.List;

public class SearchWordAdapter extends BaseAdapter {

    private Context context;
    private List<String> list;
    private LayoutInflater inflater;
    private MyHolder viewHolder;

    public SearchWordAdapter(Context context, List<String> list){
        this.context = context;
        this.list = list;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public String getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            convertView = inflater.inflate(R.layout.search_listview_item,null);

            viewHolder = new MyHolder();
            viewHolder.label = (TextView)convertView.findViewById(R.id.label);
            convertView.setTag(viewHolder);
        }
        else{
            viewHolder = (MyHolder) convertView.getTag();
        }
        viewHolder.label.setText(list.get(position));

        return convertView;
    }

    class MyHolder{
        public TextView label;
    }
}
