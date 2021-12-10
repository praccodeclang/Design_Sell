package com.taewon.shoppingmall.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.firebase.storage.FirebaseStorage;
import com.taewon.shoppingmall.R;

import java.util.ArrayList;

public class SalesPictureRecyclerAdapter extends RecyclerView.Adapter<SalesPictureRecyclerAdapter.MyViewHolder>{

    Context context;
    ArrayList<Bitmap> bitmaps;
    FirebaseStorage storage;
    public SalesPictureRecyclerAdapter(Context context, ArrayList<Bitmap> bitmaps){
        this.context = context;
        this.bitmaps = bitmaps;
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
        Glide.with(context)
                .load(bitmaps.get(position))
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
                .into(holder.iv_default_img);
    }

    @Override
    public int getItemCount() {
        return bitmaps.size();
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
