package com.taewon.shoppingmall.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.taewon.shoppingmall.R;
import com.taewon.shoppingmall.User;
import com.taewon.shoppingmall.activity.ProfileViewActivity;
import com.taewon.shoppingmall.activity.WebViewActivity;
import com.taewon.shoppingmall.item.AdsItem;

import java.util.ArrayList;

public class UserProfileRecyclerAdapter extends RecyclerView.Adapter<UserProfileRecyclerAdapter.ViewHolder> {
    Context context;
    ArrayList<User> items;
    FirebaseStorage storage;
    public UserProfileRecyclerAdapter(Context context, ArrayList<User> items){
        this.context = context;
        this.items = items;
        storage = FirebaseStorage.getInstance();
    }

    @NonNull
    @Override
    public UserProfileRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.main_user_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserProfileRecyclerAdapter.ViewHolder holder, int position) {
        User item = items.get(holder.getLayoutPosition());
        StorageReference storageRef = storage.getReference();
        storageRef.child(item.getPhotoUrl()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                if(((Activity)context).isFinishing()){
                    return;
                }
                Glide.with(context)
                        .load(uri)
                        .circleCrop()
                        .apply(new RequestOptions().circleCrop())
                        .into(holder.iv_userItemProfile);
            }
        });
        holder.tv_userItemNameText.setText(item.getUsername());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        LinearLayout li_userProfileWrap;
        ImageView iv_userItemProfile;
        TextView tv_userItemNameText;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            li_userProfileWrap = itemView.findViewById(R.id.li_userProfileWrap);
            iv_userItemProfile = itemView.findViewById(R.id.iv_userItemProfile);
            tv_userItemNameText = itemView.findViewById(R.id.tv_userItemNameText);

            li_userProfileWrap.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, ProfileViewActivity.class);
                    int pos = getAdapterPosition();
                    if(pos != Adapter.NO_SELECTION){
                        User user = items.get(getAdapterPosition());
                        intent.putExtra("UserData", user);
                        context.startActivity(intent);
                    }
                }
            });
        }
    }
}
