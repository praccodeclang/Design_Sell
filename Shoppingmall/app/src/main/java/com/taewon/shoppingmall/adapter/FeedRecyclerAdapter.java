package com.taewon.shoppingmall.adapter;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.taewon.shoppingmall.R;
import com.taewon.shoppingmall.item.BoardItem;

import java.util.ArrayList;

public class FeedRecyclerAdapter extends RecyclerView.Adapter<FeedRecyclerAdapter.MyViewHolder>{

    Context context;
    ArrayList<BoardItem> boardItems;
    FirebaseStorage storage;
    public FeedRecyclerAdapter(Context context, ArrayList<BoardItem> boardItems){
        this.context = context;
        this.boardItems = boardItems;
        storage = FirebaseStorage.getInstance();
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.default_img_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        storage.getReference("Board/" + boardItems.get(position).getBoardID()).list(1)
                .addOnSuccessListener(new OnSuccessListener<ListResult>() {
                    @Override
                    public void onSuccess(ListResult listResult) {
                        listResult.getItems().get(0).getDownloadUrl()
                                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        if(((Activity)context).isFinishing()){
                                            return;
                                        }
                                        Glide.with(context)
                                                .load(uri)
                                                .override(300,300)
                                                .placeholder(R.drawable.ic_loading)
                                                .error(R.drawable.ic_warning)
                                                .into(holder.iv_default_img);
                                        holder.iv_default_img.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if(((Activity)context).isFinishing()){
                            return;
                        }
                        Glide.with(context)
                                .load(R.drawable.ic_warning)
                                .into(holder.iv_default_img);
                    }
                });
    }

    @Override
    public int getItemCount() {
        return boardItems.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        ImageView iv_default_img;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            iv_default_img = itemView.findViewById(R.id.iv_default_img);
        }
    }
}
