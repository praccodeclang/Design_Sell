package com.taewon.shoppingmall.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.taewon.shoppingmall.R;
import com.taewon.shoppingmall.item.BoardItem;

import java.util.ArrayList;
import java.util.List;

public class BoardRecyclerAdapter extends RecyclerView.Adapter<BoardRecyclerAdapter.MyViewHolder> {
    Context context;
    ArrayList<BoardItem> items;
    FirebaseStorage storage;
    FirebaseDatabase database;
    public BoardRecyclerAdapter(Context context, ArrayList<BoardItem> items){
        this.context = context;
        this.items = items;
        storage = FirebaseStorage.getInstance();
        database = FirebaseDatabase.getInstance();
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.board_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        BoardItem item = items.get(position);
        StorageReference storageRef = storage.getReference();
        DatabaseReference databaseRef = database.getReference();
        storageRef.child("Profile/" + item.uid +"/profile.png").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(context)
                        .load(uri)
                        .circleCrop()
                        .apply(new RequestOptions().circleCrop())
                        .into(holder.iv_boardUserProfile);
            }
        });
        holder.tv_nickname.setText(item.getUsername());
        holder.tv_uploadTime.setText("1분 전");
        //보드이미지 가져오기.
//        storageRef.child()
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        LinearLayout li_board_wrap;
        ImageView iv_boardImg;
        ImageView iv_boardUserProfile;
        TextView tv_nickname;
        TextView tv_uploadTime;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            li_board_wrap = itemView.findViewById(R.id.li_board_wrap);
            iv_boardImg = itemView.findViewById(R.id.iv_boardImg);
            iv_boardUserProfile = itemView.findViewById(R.id.iv_boardUserProfile);
            tv_nickname = itemView.findViewById(R.id.tv_nickname);
            tv_uploadTime = itemView.findViewById(R.id.tv_uploadTime);

            li_board_wrap.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }
    }
}
