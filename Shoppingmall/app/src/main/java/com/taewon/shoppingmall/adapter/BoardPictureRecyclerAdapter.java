package com.taewon.shoppingmall.adapter;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
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
        if(refs.size() < 3){
            ViewGroup.LayoutParams params = view.findViewById(R.id.iv_boardImg).getLayoutParams();
            params.height = params.height * 2;
            view.findViewById(R.id.iv_boardImg).requestLayout();
        }
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        if(position>4){
            //5개까지만 미리보기 제공.
            return;
        }
        Log.d("보드 이미지 다운로드", "시작");
        refs.get(position).getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if(task.isSuccessful()){
                    Glide.with(context)
                            .load(task.getResult())
                            .placeholder(R.drawable.ic_loading)
                            .into(holder.iv_boardImg);
                }
            }
        });
        Log.d("보드 이미지 다운로드", "완료");
    }

    @Override
    public int getItemCount() {
        return refs.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        ImageView iv_boardImg;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            iv_boardImg = itemView.findViewById(R.id.iv_boardImg);
        }
    }
}
