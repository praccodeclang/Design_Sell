package com.taewon.shoppingmall.adapter;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.taewon.shoppingmall.R;
import com.taewon.shoppingmall.activity.BoardViewActivity;
import com.taewon.shoppingmall.item.BoardItem;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

public class BoardRecyclerAdapter extends RecyclerView.Adapter<BoardRecyclerAdapter.MyViewHolder> {
    Context context;
    ArrayList<BoardItem> items;
    FirebaseStorage storage;
    FirebaseDatabase database;
    FirebaseAuth mAuth;
    ArrayList<StorageReference> boardImgRefs;

    public BoardRecyclerAdapter(Context context, ArrayList<BoardItem> items){
        this.context = context;
        this.items = items;
        mAuth = FirebaseAuth.getInstance();
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
        checkLike(holder.lottie_like, item);
        checkCart(holder.lottie_addCart, item);
        //like check
        boardImgRefs = new ArrayList<>();

        //유저 프로필 가져오기
        StorageReference storageRef = storage.getReference();
        storageRef.child("Profile/" + item.getUid() +"/profile.png").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                if(((Activity)context).isFinishing()){
                    return;
                }
                Glide.with(context)
                        .load(uri)
                        .circleCrop()
                        .apply(new RequestOptions().circleCrop())
                        .into(holder.iv_boardUserProfile);
            }
        });


        holder.tv_nickname.setText(item.getUsername());

        //업로드 시간 텍스트 변경
        //현재시간
        SimpleDateFormat now = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar calendar = Calendar.getInstance();
        Date date1 = calendar.getTime();
        now.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
        holder.tv_uploadTime.setText(calUploadDate(now.format(date1), item.getDateString()));
        holder.tv_boardTitle.setText(item.getTitle());

        BoardPictureRecyclerAdapter pictureRecyclerAdapter = new BoardPictureRecyclerAdapter(context, boardImgRefs);
        holder.rv_boardImgs.setAdapter(pictureRecyclerAdapter);

    }

    void likeAnim(LottieAnimationView lottie, boolean isLike){
        ValueAnimator animator;
        if(isLike) animator = ValueAnimator.ofFloat(0f, 0.5f).setDuration(500);
        else animator = ValueAnimator.ofFloat(0.5f, 1.0f).setDuration(500);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                lottie.setProgress((Float) valueAnimator.getAnimatedValue());
            }
        });
        animator.start();
    }

    void checkLike(LottieAnimationView lottie, BoardItem item){
        if(item.getLikeUsers().containsKey(mAuth.getCurrentUser().getUid())){
            likeAnim(lottie, true);
            return;
        }
        likeAnim(lottie, false);
    }

    void cartAnim(LottieAnimationView lottie, boolean isAdded){
        ValueAnimator animator;
        if(isAdded) animator = ValueAnimator.ofFloat(0f, 0.5f).setDuration(500);
        else animator = ValueAnimator.ofFloat(0.5f, 1.0f).setDuration(500);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                lottie.setProgress((Float) valueAnimator.getAnimatedValue());
            }
        });
        animator.start();
    }

    void checkCart(LottieAnimationView lottie, BoardItem item){
        if(item.getLikeUsers().containsKey(mAuth.getCurrentUser().getUid())){
            cartAnim(lottie, true);
            return;
        }
        cartAnim(lottie, false);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }


    private String calUploadDate(String date1, String date2){
        Date format1;
        Date format2;
        try{
            format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(date1);
            format2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(date2);
            long diffSec = (format1.getTime() - format2.getTime()) / 1000; //초 차이

            long diffDays = diffSec / (24*60*60); // 일 수 차이
            if(diffDays > 0){
                return diffDays +"일 전";
            }

            long diffHour = (format1.getTime() - format2.getTime()) / 3600000; //시간 차이
            if(diffHour > 0){
                return diffHour + "시간 전";
            }

            long diffMin = (format1.getTime() - format2.getTime()) / 60000; //분 차이
            return diffMin+"분 전";
        }catch (Exception e){
            e.printStackTrace();
            Log.e("시간 에러", e.getMessage());
        }
        return "";
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        CardView cv_board_wrap;
        RecyclerView rv_boardImgs;
        ImageView iv_boardUserProfile;
        TextView tv_nickname;
        TextView tv_uploadTime;
        TextView tv_boardTitle;
        LottieAnimationView lottie_addCart;
        LottieAnimationView lottie_like;
        BoardItem item;
        BoardPictureRecyclerAdapter pictureRecyclerAdapter;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_boardTitle = itemView.findViewById(R.id.tv_boardTitle);
            cv_board_wrap = itemView.findViewById(R.id.cv_board_wrap);
            rv_boardImgs = itemView.findViewById(R.id.rv_boardImgs);
            iv_boardUserProfile = itemView.findViewById(R.id.iv_boardUserProfile);
            tv_nickname = itemView.findViewById(R.id.tv_nickname);
            tv_uploadTime = itemView.findViewById(R.id.tv_uploadTime);
            lottie_addCart = itemView.findViewById(R.id.lottie_board_addCart);
            lottie_like = itemView.findViewById(R.id.lottie_board_like);


            //전체 레이아웃 클릭
            cv_board_wrap.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getAdapterPosition();
                    if(pos != RecyclerView.NO_POSITION){
                        BoardItem item = items.get(pos);
                        Intent intent = new Intent(context, BoardViewActivity.class);
                        intent.putExtra("BoardItem", item);
                        context.startActivity(intent);
                        ((Activity)context).overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                    }
                }
            });
            rv_boardImgs.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if(pos != RecyclerView.NO_POSITION){
                        BoardItem item = items.get(pos);
                        Intent intent = new Intent(context, BoardViewActivity.class);
                        intent.putExtra("BoardItem", item);
                        context.startActivity(intent);
                        ((Activity)context).overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                    }
                }
            });
            //좋아요 버튼
            lottie_like.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getAdapterPosition();
                    if(pos != RecyclerView.NO_POSITION){
                        like(lottie_like, items.get(pos));
                    }
                }
            });

            //장바구니 버튼
            lottie_addCart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getAdapterPosition();
                    if(pos != RecyclerView.NO_POSITION){
                        addEraseCart(lottie_addCart, items.get(pos));
                    }
                }
            });
        }

        
        //함수
        void like(LottieAnimationView lottie, BoardItem item){
            //좋아요 추가 & 제거
            boolean isContains = item.getLikeUsers().containsKey(mAuth.getCurrentUser().getUid());
            if(isContains){
                item.setStarCount(item.getStarCount() - 1);
                item.getLikeUsers().remove(mAuth.getCurrentUser().getUid());
                DatabaseReference ref = database.getReference("Board/"+item.getBoardID());
                ref.setValue(item);
                likeAnim(lottie, false);
            }
            else{
                item.setStarCount(item.getStarCount() + 1);
                item.getLikeUsers().put(mAuth.getCurrentUser().getUid(), true);
                DatabaseReference ref = database.getReference("Board/" + item.getBoardID());
                ref.setValue(item);
                likeAnim(lottie, true);
            }
            notifyDataSetChanged();
        }

        void addEraseCart(LottieAnimationView lottie, BoardItem item){
            //장바구니 추가 & 제거 11.28 여기부터 시작하세요.
            DatabaseReference databaseRef = database.getReference("Cart/"+item.getBoardID());
            databaseRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if(task.isSuccessful() && task.getResult().exists()){
                        Map<String, Object> map = (Map<String, Object>)task.getResult().getValue();
                        if(map == null){
                            map = new HashMap<>();
                        }

                        if(map.keySet().contains(mAuth.getCurrentUser().getUid())){
                            map.remove(mAuth.getCurrentUser().getUid());
                            databaseRef.setValue(map);
                            cartAnim(lottie, false);
                        }
                        else{
                            map.put(mAuth.getCurrentUser().getUid(), true);
                            databaseRef.setValue(map);
                            cartAnim(lottie, true);
                        }
                    }
                }
            });
            notifyDataSetChanged();
        }

        void getBoardImg(){
            //여기부터
            pictureRecyclerAdapter = new BoardPictureRecyclerAdapter(context, boardImgRefs);
            StorageReference storageRef = storage.getReference();
            storageRef.child("Board/"+item.getBoardID()+"/").listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
                @Override
                public void onSuccess(ListResult listResult) {
                    GridLayoutManager manager;
                    for(StorageReference item : listResult.getItems()){
                        boardImgRefs.add(item);
                        if(boardImgRefs.size() == 5){
                            break;
                        }
                    }
                    if(boardImgRefs.size() > 3){
                        manager = new GridLayoutManager(context, 6){
                            @Override
                            public boolean canScrollVertically() {
                                return false;
                            }
                        };
                        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                            @Override
                            public int getSpanSize(int position) {
                                int gridPosition = position % 5;
                                Log.d("GridPosition", Integer.toString(gridPosition));
                                switch (gridPosition){
                                    case 0:
                                    case 1:
                                    case 2:
                                        return 2;
                                    case 3:
                                    case 4:
                                        return 3;
                                }
                                return 1;
                            }
                        });
                    }
                    else{
                        manager = new GridLayoutManager(context, 2){
                            @Override
                            public boolean canScrollVertically() {
                                return false;
                            }
                        };;
                        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                            @Override
                            public int getSpanSize(int position) {
                                switch (position){
                                    case 0:
                                    case 1:
                                        return 1;
                                    case 2:
                                        return 2;
                                }
                                return 1;
                            }
                        });
                    }
                    rv_boardImgs.setLayoutManager(manager);
                    pictureRecyclerAdapter.notifyDataSetChanged();
                }
            });
        }
    }
}
