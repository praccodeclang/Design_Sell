package com.taewon.shoppingmall.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.StorageReference;
import com.taewon.shoppingmall.R;

import java.util.ArrayList;

public class DownloadPictureRecyclerAdapter extends RecyclerView.Adapter<DownloadPictureRecyclerAdapter.MyViewHolder> {

    Context context;
    ArrayList<StorageReference> refs;
    public DownloadPictureRecyclerAdapter(Context context, ArrayList<StorageReference> refs){
        this.context = context;
        this.refs = refs;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.download_img_item, parent, false);
        Animation animation = AnimationUtils.loadAnimation(context, R.anim.board_slide_in);
        view.setAnimation(animation);
        return new DownloadPictureRecyclerAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        refs.get(position).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(context)
                .load(uri)
                .error(R.drawable.ic_warning)
                .into(holder.iv_download_img);
            }
        });

        holder.iv_download_img.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return refs.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        ImageView iv_download_img;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            iv_download_img = itemView.findViewById(R.id.iv_download_img);
        }
    }
}
