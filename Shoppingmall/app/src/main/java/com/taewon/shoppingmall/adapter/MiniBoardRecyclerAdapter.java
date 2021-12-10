package com.taewon.shoppingmall.adapter;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Adapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.taewon.shoppingmall.R;
import com.taewon.shoppingmall.activity.BoardViewActivity;
import com.taewon.shoppingmall.item.BoardItem;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class MiniBoardRecyclerAdapter extends RecyclerView.Adapter<MiniBoardRecyclerAdapter.MyViewHolder> {
    Context context;
    ArrayList<BoardItem> items;
    FirebaseStorage storage;
    FirebaseDatabase database;
    FirebaseAuth mAuth;

    public MiniBoardRecyclerAdapter(Context context, ArrayList<BoardItem> items){
        this.context = context;
        this.items = items;
        mAuth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        database = FirebaseDatabase.getInstance();
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.mini_board_item, parent, false);
        Animation animation = AnimationUtils.loadAnimation(context, R.anim.board_slide_in);
        view.setAnimation(animation);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        BoardItem item = items.get(position);
        holder.tv_mini_board_title.setText(item.getTitle());
        holder.tv_mini_board_body.setText(item.getBody());
        holder.li_mini_board_wrap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, BoardViewActivity.class);
                intent.putExtra("BoardItem", item);
                context.startActivity(intent);
            }
        });
        storage.getReference("Board/"+item.getBoardID()).list(1).addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
                if(((Activity)context).isFinishing()){
                    return;
                }
                StorageReference ref = null;
                try{
                    ref = listResult.getItems().get(0);
                }
                catch (Exception e){
                    e.printStackTrace();
                }

                if(ref == null){
                    holder.iv_mini_board_img.setImageResource(R.drawable.ic_warning);
                    return;
                }
                ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        if(((Activity)context).isFinishing()){
                            return;
                        }
                        Glide.with(context)
                                .load(uri)
                                .error(R.drawable.ic_warning)
                                .into(holder.iv_mini_board_img);
                    }
                });
            }
        });

        //현재시간
        SimpleDateFormat now = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar calendar = Calendar.getInstance();
        Date date1 = calendar.getTime();
        now.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
        holder.tv_mini_board_date.setText(calUploadDate(now.format(date1), item.getDateString()));
        if(item.getPrice() == 0){
            holder.tv_mini_board_price.setText("무료");
        }
        holder.tv_mini_board_price.setText(item.getPrice()+"");
    }

    @Override
    public int getItemCount() {
        return items.size();
    }


    private String calUploadDate(String date1, String date2){
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

    public class MyViewHolder extends RecyclerView.ViewHolder{
        FrameLayout li_mini_board_wrap;
        ImageView iv_mini_board_img;
        TextView tv_mini_board_title;
        TextView tv_mini_board_body;
        TextView tv_mini_board_date;
        TextView tv_mini_board_price;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            li_mini_board_wrap = itemView.findViewById(R.id.li_mini_board_wrap);
            iv_mini_board_img = itemView.findViewById(R.id.iv_mini_board_img);
            tv_mini_board_title = itemView.findViewById(R.id.tv_mini_board_title);
            tv_mini_board_body = itemView.findViewById(R.id.tv_mini_board_body);
            tv_mini_board_date = itemView.findViewById(R.id.tv_mini_board_date);
            tv_mini_board_price = itemView.findViewById(R.id.tv_mini_board_price);
        }
    }
}
