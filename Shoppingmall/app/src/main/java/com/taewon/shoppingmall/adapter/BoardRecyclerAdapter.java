package com.taewon.shoppingmall.adapter;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.GridLayout;
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
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
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
import com.taewon.shoppingmall.dialog.BoardEditDialog;
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
    BoardPictureRecyclerAdapter pictureRecyclerAdapter;
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
        Animation animation = AnimationUtils.loadAnimation(context, R.anim.board_slide_in);
        view.setAnimation(animation);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        BoardItem item = items.get(holder.getLayoutPosition());
        //like check
        checkLike(holder.lottie_like, item);
        checkCart(holder.lottie_addCart, item);

        //이미지 로드
        ArrayList<StorageReference> boardImgRefs = new ArrayList<>();
        StorageReference storageRef = storage.getReference();
        storageRef.child("Board/"+item.getBoardID()+"/")
                .listAll()
                .addOnSuccessListener(new OnSuccessListener<ListResult>() {
                    @Override
                    public void onSuccess(ListResult listResult) {
                        for(StorageReference item : listResult.getItems()){
                            boardImgRefs.add(item);
                            Log.d("이미지 레퍼런스", item.getPath());
                            if(boardImgRefs.size() == 4){
                                break;
                            }
                        }
                        pictureRecyclerAdapter = new BoardPictureRecyclerAdapter(context, boardImgRefs);
                        holder.rv_boardImgs.setAdapter(pictureRecyclerAdapter);
                        holder.rv_boardImgs.setLayoutManager(getGridLayoutManager(boardImgRefs.size()));
                        pictureRecyclerAdapter.notifyDataSetChanged();
                    }
                });


        //유저 프로필 가져오기
        storageRef.child("Profile/" + item.getUid() +"/profile.png").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                if(((Activity)context).isFinishing()){
                    return;
                }
                Log.d("URL", uri.toString());
                Glide.with(context)
                        .load(uri)
                        .apply(new RequestOptions().circleCrop())
                        .circleCrop()
                        .into(holder.iv_boardUserProfile);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if(((Activity)context).isFinishing()){
                    return;
                }
                Glide.with(context)
                        .load(R.drawable.test_profile)
                        .apply(new RequestOptions().circleCrop())
                        .circleCrop()
                        .into(holder.iv_boardUserProfile);
            }
        });

        //유저 이름 텍스트 변경
        holder.tv_nickname.setText(item.getUsername());

        //업로드 시간 텍스트 변경
        //현재시간
        SimpleDateFormat now = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar calendar = Calendar.getInstance();
        Date date1 = calendar.getTime();
        now.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
        holder.tv_uploadTime.setText(calUploadDate(now.format(date1), item.getDateString()));
        holder.tv_boardTitle.setText(item.getTitle());

        //전체 레이아웃 클릭
        holder.cv_board_wrap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, BoardViewActivity.class);
                intent.putExtra("BoardItem", item);
                context.startActivity(intent);
                ((Activity)context).overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
            }
        });
        holder.rv_boardImgs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, BoardViewActivity.class);
                intent.putExtra("BoardItem", item);
                context.startActivity(intent);
                ((Activity)context).overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
            }
        });
        //좋아요 버튼
        holder.lottie_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                like(holder.lottie_like, item);
            }
        });

        //장바구니 버튼
        holder.lottie_addCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addEraseCart(holder.lottie_addCart, item);
            }
        });

        holder.iv_board_etc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new BoardEditDialog(context, item).show();
            }
        });
    }

    private GridLayoutManager getGridLayoutManager(int size){
        GridLayoutManager manager = new GridLayoutManager(context, 2){
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        if(size == 1){
            manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup(){
                @Override
                public int getSpanSize(int position) {
                    return 2;
                }
            });
            return manager;
        }
        else if(size != 4){
            manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup(){
                @Override
                public int getSpanSize(int position) {
                    switch (position) {
                        case 0:
                        case 1:
                            return 1;
                        case 2:
                            return 2;
                    }
                    return 0;
                }
            });
            return manager;
        }
        else {
            manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    return 1;
                }
            });
            return manager;
        }
    }

    void like(LottieAnimationView lottie, BoardItem item){
        //좋아요 추가 & 제거
        DatabaseReference ref = database.getReference("Board/" + item.getBoardID());
        boolean isContains = item.getLikeUsers().containsKey(mAuth.getCurrentUser().getUid());
        if(isContains){
            item.setStarCount(item.getStarCount() - 1);
            item.getLikeUsers().remove(mAuth.getCurrentUser().getUid());
            likeAnim(lottie, false);
        }
        else{
            item.setStarCount(item.getStarCount() + 1);
            item.getLikeUsers().put(mAuth.getCurrentUser().getUid(), true);
            likeAnim(lottie, true);
        }

        ref.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                BoardItem instance = currentData.getValue(BoardItem.class);
                if(instance == null){
                    return Transaction.success(currentData);
                }
                currentData.setValue(item);
                return  Transaction.success(currentData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {

            }
        });
    }

    void addEraseCart(LottieAnimationView lottie, BoardItem item){
        //장바구니 추가 & 제거
        DatabaseReference databaseRef = database.getReference("Cart/"+mAuth.getCurrentUser().getUid());
        databaseRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(task.isSuccessful()){
                    Map<String, Object> map = (Map<String, Object>) task.getResult().getValue();
                    if(map == null){
                        Map<String, Object> instance = new HashMap<>();
                        instance.put(item.getBoardID(), true);
                        databaseRef.setValue(instance).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                cartAnim(lottie, true);
                                Toast.makeText(context, "장바구니에 추가되었습니다.", Toast.LENGTH_SHORT).show();
                            }
                        });
                        return;
                    }
                    if(map.keySet().contains(item.getBoardID())){
                        map.remove(item.getBoardID());
                        databaseRef.setValue(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                cartAnim(lottie, false);
                                Toast.makeText(context, "장바구니에서 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                    else{
                        map.put(item.getBoardID(), true);
                        databaseRef.setValue(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                cartAnim(lottie, true);
                                Toast.makeText(context, "장바구니에 추가되었습니다.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }

                return;
//                if(data.keySet().contains(item.getBoardID())){
//                    data.remove(item.getBoardID());
//                    cartAnim(lottie, false);
//                }
//                else{
//                    data.put(item.getBoardID(), true);
//                    databaseRef.setValue(data).addOnSuccessListener(new OnSuccessListener<Void>() {
//                        @Override
//                        public void onSuccess(Void unused) {
//                            cartAnim(lottie, true);
//                        }
//                    });
//                }
            }
        });
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
        if(isAdded) animator = ValueAnimator.ofFloat(0f, 0.25f).setDuration(1000);
        else animator = ValueAnimator.ofFloat(0.45f, 0.75f).setDuration(500);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                lottie.setProgress((Float) valueAnimator.getAnimatedValue());
            }
        });
        animator.start();
    }

    void checkCart(LottieAnimationView lottie, BoardItem item){
        DatabaseReference ref = database.getReference();
        ref.child("Cart").child(mAuth.getCurrentUser().getUid()).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                Map<String, Object> dataMap = (Map<String, Object>) dataSnapshot.getValue();
                if(dataMap == null){
                    cartAnim(lottie, false);
                    return;
                }
                if(dataMap.keySet().contains(item.getBoardID())){
                    cartAnim(lottie, true);
                }
                else{
                    cartAnim(lottie, false);
                }
            }
        });
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
        ImageView iv_board_etc;
        LottieAnimationView lottie_addCart;
        LottieAnimationView lottie_like;
        TextView tv_myItem;


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
            iv_board_etc = itemView.findViewById(R.id.iv_board_etc);
            tv_myItem = itemView.findViewById(R.id.tv_myItem);
        }
    }
}
