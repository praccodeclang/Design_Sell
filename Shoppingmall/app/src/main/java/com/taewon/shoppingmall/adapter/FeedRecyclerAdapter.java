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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.taewon.shoppingmall.R;
import com.taewon.shoppingmall.activity.BoardViewActivity;
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
                                                .listener(new RequestListener<Drawable>() {
                                                    @Override
                                                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                                        return false;
                                                    }

                                                    @Override
                                                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                                        holder.lottie_loading_progress.pauseAnimation();
                                                        holder.lottie_loading_progress.setVisibility(View.GONE);
                                                        return false;
                                                    }
                                                })
                                                .error(R.drawable.ic_warning)
                                                .into(holder.iv_default_img);
                                        GridLayoutManager.LayoutParams layoutParams = (GridLayoutManager.LayoutParams)holder.itemView.getLayoutParams();
                                        layoutParams.height = layoutParams.width;
                                        holder.itemView.requestLayout();
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
        holder.iv_default_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, BoardViewActivity.class);
                intent.putExtra("BoardItem", boardItems.get(position));
                context.startActivity(intent);
                ((Activity)context).overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
            }
        });
    }

    @Override
    public int getItemCount() {
        return boardItems.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        ImageView iv_default_img;
        LottieAnimationView lottie_loading_progress;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            iv_default_img = itemView.findViewById(R.id.iv_default_img);
            lottie_loading_progress = itemView.findViewById(R.id.lottie_loading_progress);
        }
    }
}
