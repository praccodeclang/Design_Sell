package com.taewon.shoppingmall.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.taewon.shoppingmall.R;
import com.taewon.shoppingmall.activity.WebViewActivity;
import com.taewon.shoppingmall.item.AdsItem;

import java.util.ArrayList;

public class AdsViewPagerAdapter extends RecyclerView.Adapter<AdsViewPagerAdapter.ViewHolder> {
    Context context;
    ArrayList<AdsItem> items;
    FirebaseStorage storage;
    public AdsViewPagerAdapter(Context context, ArrayList<AdsItem> items){
        this.context = context;
        this.items = items;
        storage = FirebaseStorage.getInstance();
    }

    @NonNull
    @Override
    public AdsViewPagerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.main_viewpager_ads, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdsViewPagerAdapter.ViewHolder holder, int position) {
        StorageReference ref = FirebaseStorage.getInstance().getReference(items.get(position).getImgRef());
        Log.d("레퍼런스", String.valueOf(ref));
        ref.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                Glide.with(context)
                    .load(task.getResult())
                    .placeholder(R.drawable.ic_loading)
                    .into(holder.iv_ads);
            }
        });
        holder.iv_ads.setTag(items.get(position).getUrl());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView iv_ads;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            iv_ads = itemView.findViewById(R.id.iv_ads);
            iv_ads.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, WebViewActivity.class);
                    intent.putExtra("url", iv_ads.getTag().toString());
                    context.startActivity(intent);
                }
            });
        }
    }
}
