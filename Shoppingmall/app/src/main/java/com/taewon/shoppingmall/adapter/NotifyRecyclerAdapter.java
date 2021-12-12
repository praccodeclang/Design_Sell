package com.taewon.shoppingmall.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.taewon.shoppingmall.R;
import com.taewon.shoppingmall.activity.BoardViewActivity;
import com.taewon.shoppingmall.item.BoardItem;
import com.taewon.shoppingmall.item.NotifyItem;
import com.taewon.shoppingmall.util.DateUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class NotifyRecyclerAdapter extends RecyclerView.Adapter<NotifyRecyclerAdapter.MyViewHolder>{

    Context context;
    ArrayList<NotifyItem> items;
    public NotifyRecyclerAdapter(Context context, ArrayList<NotifyItem> items){
        this.context = context;
        this.items = items;
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notify_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        NotifyItem item = items.get(position);
        holder.tv_notify_title.setText(item.getNotifyTitle());
        holder.tv_notify_text.setText(item.getNotifyText());

        SimpleDateFormat now = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar calendar = Calendar.getInstance();
        Date date1 = calendar.getTime();
        now.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
        holder.tv_notify_dateString.setText(DateUtil.calUploadDate(now.format(date1), item.getDateString()));
        if(item.getIsRead()){
            holder.iv_isRead_circle.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        CardView card_notify_wrap;
        TextView tv_notify_title;
        TextView tv_notify_text;
        TextView tv_notify_dateString;
        ImageView iv_isRead_circle;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            card_notify_wrap = itemView.findViewById(R.id.card_notify_wrap);
            iv_isRead_circle = itemView.findViewById(R.id.iv_isRead_circle);
            tv_notify_title = itemView.findViewById(R.id.tv_notify_title);
            tv_notify_text = itemView.findViewById(R.id.tv_notify_text);
            tv_notify_dateString = itemView.findViewById(R.id.tv_notify_dateString);
        }
    }
}
