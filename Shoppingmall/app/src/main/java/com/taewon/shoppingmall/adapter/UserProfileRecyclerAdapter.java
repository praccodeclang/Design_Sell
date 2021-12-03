package com.taewon.shoppingmall.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.taewon.shoppingmall.R;
import com.taewon.shoppingmall.item.User;
import com.taewon.shoppingmall.activity.ProfileViewActivity;

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
        Animation animation = AnimationUtils.loadAnimation(context, R.anim.board_slide_in);
        view.setAnimation(animation);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserProfileRecyclerAdapter.ViewHolder holder, int position) {
        User item = items.get(holder.getLayoutPosition());

        holder.li_userProfileWrap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ProfileViewActivity.class);
                intent.putExtra("UserData", item);
                context.startActivity(intent);
            }
        });
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
        }
    }
}
