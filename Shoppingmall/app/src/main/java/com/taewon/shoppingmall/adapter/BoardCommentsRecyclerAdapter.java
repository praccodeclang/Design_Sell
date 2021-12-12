package com.taewon.shoppingmall.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.taewon.shoppingmall.R;
import com.taewon.shoppingmall.dialog.CommentDeleteDialog;
import com.taewon.shoppingmall.item.BoardCommentItem;
import com.taewon.shoppingmall.item.BoardItem;
import com.taewon.shoppingmall.item.User;
import com.taewon.shoppingmall.util.DateUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class BoardCommentsRecyclerAdapter extends RecyclerView.Adapter<BoardCommentsRecyclerAdapter.MyViewHolder>{

    Context context;
    ArrayList<BoardCommentItem> items;
    FirebaseAuth mAuth;
    FirebaseStorage storage;
    FirebaseDatabase database;
    public BoardCommentsRecyclerAdapter(Context context, ArrayList<BoardCommentItem> items){
        this.context = context;
        this.items = items;
        mAuth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        database = FirebaseDatabase.getInstance();
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.board_comments_item, parent, false);
        Animation animation = AnimationUtils.loadAnimation(context, R.anim.board_slide_in);
        view.setAnimation(animation);
        return new MyViewHolder(view);
    }



    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        BoardCommentItem item = items.get(position);
        storage.getReference("Profile").child(item.getUid()).child("/profile.png")
                .getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                if(((Activity)context).isFinishing()){
                    return;
                }
                Glide.with(context)
                        .load(uri)
                        .apply(new RequestOptions().circleCrop())
                        .error(R.drawable.test_profile)
                        .into(holder.iv_comments_user_profile);
            }
        })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Glide.with(context)
                        .load(R.drawable.test_profile)
                        .apply(new RequestOptions().circleCrop())
                        .into(holder.iv_comments_user_profile);
            }
        });

        database.getReference("Board").child(item.getBoardID()).get()
                .addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                    @Override
                    public void onSuccess(DataSnapshot dataSnapshot) {
                        BoardItem boardItem = dataSnapshot.getValue(BoardItem.class);
                        holder.iv_comments_delete.setVisibility(View.GONE);

                        //12.12 여기부터 (댓글조건 맞추기)
                        if(item.getUid().equals(mAuth.getCurrentUser().getUid()) || boardItem.getUid().equals(mAuth.getCurrentUser().getUid())){
                            //1. 글 작성자 이거나, 내가 작성한 댓글이면 삭제 권한.
                            holder.iv_comments_delete.setVisibility(View.VISIBLE);
                        }

                        if(boardItem.getUid().equals(item.getUid())){
                            //2. 판매자라면?
                            holder.tv_comments_username.setText("판매자");
                            holder.tv_comments_username.setTextColor(Color.CYAN);
                        }
                        else{
                            holder.tv_comments_username.setText(item.getUsername());
                            holder.tv_comments_username.setTextColor(Color.BLACK);
                        }

                        holder.tv_comments_comment.setText(item.getComment());

                        SimpleDateFormat now = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        Calendar calendar = Calendar.getInstance();
                        Date date1 = calendar.getTime();
                        now.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
                        holder.tv_comments_date.setText(DateUtil.calUploadDate(now.format(date1), item.getDateString()));
                    }
                });
        holder.iv_comments_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new CommentDeleteDialog(context, item, new CommentDeleteDialog.DialogClickListener() {
                    @Override
                    public void onDelete() {
                        items.remove(item);
                        notifyDataSetChanged();
                    }
                })
                .show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        ImageView iv_comments_user_profile;
        TextView tv_comments_username;
        TextView tv_comments_comment;
        TextView tv_comments_date;
        ImageView iv_comments_delete;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            iv_comments_user_profile = itemView.findViewById(R.id.iv_comments_user_profile);
            tv_comments_username = itemView.findViewById(R.id.tv_comments_username);
            tv_comments_comment = itemView.findViewById(R.id.tv_comments_comment);
            tv_comments_date = itemView.findViewById(R.id.tv_comments_date);
            iv_comments_delete = itemView.findViewById(R.id.iv_comments_delete);
        }
    }
}
