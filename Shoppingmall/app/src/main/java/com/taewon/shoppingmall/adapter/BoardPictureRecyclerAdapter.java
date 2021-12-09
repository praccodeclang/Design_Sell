package com.taewon.shoppingmall.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.taewon.shoppingmall.R;

import java.util.ArrayList;

public class BoardPictureRecyclerAdapter extends RecyclerView.Adapter<BoardPictureRecyclerAdapter.MyViewHolder>{

    Context context;
    ArrayList<StorageReference> refs;
    FirebaseStorage storage;
    public BoardPictureRecyclerAdapter(Context context, ArrayList<StorageReference> refs){
        this.context = context;
        this.refs = refs;
        storage = FirebaseStorage.getInstance();
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.board_img_item, parent, false);
//        if(refs.size() < 3){
//            ViewGroup.LayoutParams params = view.findViewById(R.id.iv_boardImg).getLayoutParams();
//            params.height = params.height * 2;
//            view.findViewById(R.id.iv_boardImg).requestLayout();
//        }
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
//        Log.d("보드 이미지 경로", refs.get(position).getPath());
//        Log.d("보드 이미지 다운로드", "시작");
        refs.get(position).getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if(task.isSuccessful()){
                    if(((Activity)context).isFinishing()){
                        return;
                    }
                    Glide.with(context)
                            .load(task.getResult())
                            .error(R.drawable.ic_warning)
                            .listener(new RequestListener<Drawable>() {
                                @Override
                                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                    holder.lottie_board_img_loading.pauseAnimation();
                                    holder.lottie_board_img_loading.setVisibility(View.GONE);
                                    holder.board_img_wrap.setVisibility(View.VISIBLE);
                                    return false;
                                }
                            })
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(holder.iv_boardImg);
                }
            }
        });
        try{
            GridLayoutManager.LayoutParams layoutParams = (GridLayoutManager.LayoutParams)holder.itemView.getLayoutParams();
            layoutParams.height = 500;
            holder.itemView.requestLayout();
        }
        catch (Exception e){

        }

    }

    @Override
    public int getItemCount() {
        return refs.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        FrameLayout board_img_wrap;
        ImageView iv_boardImg;
        LottieAnimationView lottie_board_img_loading;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            iv_boardImg = itemView.findViewById(R.id.iv_boardImg);
            board_img_wrap = itemView.findViewById(R.id.board_img_wrap);
            lottie_board_img_loading = itemView.findViewById(R.id.lottie_board_img_loading);
        }
    }
}
