package com.taewon.shoppingmall.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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
        holder.iv_default_img.setImageBitmap(bitmaps.get(position));
    }

    @Override
    public int getItemCount() {
        return bitmaps.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        ImageView iv_default_img;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            iv_default_img = itemView.findViewById(R.id.iv_default_img);
        }
    }
}
