package com.taewon.shoppingmall.activity;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Debug;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.normal.TedPermission;
import com.taewon.shoppingmall.R;
import com.taewon.shoppingmall.dialog.LottieLoadingDialog;
import com.taewon.shoppingmall.item.User;
import com.taewon.shoppingmall.adapter.BoardPictureRecyclerAdapter;
import com.taewon.shoppingmall.item.BoardItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BoardViewActivity extends AppCompatActivity {

    Intent intent;
    BoardItem intentBoardItem;
    DatabaseReference boardDataRef;
    FirebaseAuth mAuth;
    FirebaseDatabase database;
    FirebaseStorage storage;
    LottieLoadingDialog loadingDialog;
    User writtenUser;

    SwipeRefreshLayout rl_wrapLayout;
    TextView tv_boardView_title;
    RecyclerView rv_boardViewImg;
    BoardPictureRecyclerAdapter pictureRecyclerAdapter;
    ArrayList<StorageReference> boardImgRefs;

    LinearLayout li_boardView_profile;
    ImageView iv_boardView_userImg;
    TextView iv_boardView_userName;

    TextView tv_boardView_body;
    LinearLayout li_buyBtn;
    LottieAnimationView lottie_boardView_price;
    LottieAnimationView lottie_boardView_like;
    LottieAnimationView lottie_boardView_cart;
    TextView tv_boardView_price;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board_view);
        loadingDialog = new LottieLoadingDialog(BoardViewActivity.this);
        loadingDialog.show();
        try{
            init();
            initViews();
            initListeners();
            checkLike(lottie_boardView_like, intentBoardItem);
            checkCart(lottie_boardView_cart, intentBoardItem);
        }catch (Exception e){
            loadingDialog.dismiss();
            Toast.makeText(BoardViewActivity.this, "게시글을 불러오지 못했습니다.", Toast.LENGTH_SHORT).show();
            onBackPressed();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        refresh();
    }

    void init(){
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
        boardImgRefs = new ArrayList<>();
        intent = getIntent();
        intentBoardItem = (BoardItem) intent.getSerializableExtra("BoardItem");
        boardDataRef = database.getReference().child("Board").child(intentBoardItem.getBoardID());
    }

    void initViews(){
        rl_wrapLayout = findViewById(R.id.rl_wrapLayout);
        tv_boardView_title = findViewById(R.id.tv_boardView_title);

        //릴레이티브 레이아웃 초기화 및 어댑터
        rv_boardViewImg = findViewById(R.id.rv_boardViewImg);
        pictureRecyclerAdapter = new BoardPictureRecyclerAdapter(BoardViewActivity.this, boardImgRefs);
        rv_boardViewImg.setAdapter(pictureRecyclerAdapter);
        new LinearSnapHelper().attachToRecyclerView(rv_boardViewImg);

        li_boardView_profile = findViewById(R.id.li_boardView_profile);
        iv_boardView_userImg = findViewById(R.id.iv_boardView_userImg);
        iv_boardView_userName = findViewById(R.id.iv_boardView_userName);
        tv_boardView_body = findViewById(R.id.tv_boardView_body);
        lottie_boardView_price = findViewById(R.id.lottie_boardView_price);
        lottie_boardView_like = findViewById(R.id.lottie_boardView_like);
        lottie_boardView_cart = findViewById(R.id.lottie_boardView_cart);
        li_buyBtn = findViewById(R.id.li_buyBtn);
        tv_boardView_price = findViewById(R.id.tv_boardView_price);

        database.getReference().child("Users").child(intentBoardItem.getUid()).get()
                .addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                    @Override
                    public void onSuccess(DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);
                        writtenUser = user;
                        storage.getReference(user.getPhotoUrl()).getDownloadUrl()
                                .addOnCompleteListener(new OnCompleteListener<Uri>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Uri> task) {
                                        if(task.isSuccessful()){
                                            Log.d("url", task.getResult().toString());
                                            Glide.with(BoardViewActivity.this)
                                                    .load(task.getResult())
                                                    .apply(new RequestOptions().circleCrop())
                                                    .error(R.drawable.test_profile)
                                                    .into(iv_boardView_userImg);
                                        }
                                        else{

                                        }
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Glide.with(BoardViewActivity.this)
                                        .load(R.drawable.test_profile)
                                        .error(R.drawable.test_profile)
                                        .into(iv_boardView_userImg);
                            }
                        });
                    }
                });
    }

    void initListeners(){
        li_boardView_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BoardViewActivity.this, ProfileViewActivity.class);
                intent.putExtra("UserData", writtenUser);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
            }
        });
        rl_wrapLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                DatabaseReference ref = database.getReference("Board");
                ref.child(intentBoardItem.getBoardID()).get()
                    .addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                        @Override
                        public void onSuccess(DataSnapshot dataSnapshot) {

                            BoardItem item = (BoardItem)dataSnapshot.getValue(BoardItem.class);
                            if(item != null){
                                refresh();
                                rl_wrapLayout.setRefreshing(false);
                            }
                            else{
                                Toast.makeText(BoardViewActivity.this, "새로고침에 실패했습니다.", Toast.LENGTH_SHORT).show();
                                rl_wrapLayout.setRefreshing(false);
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(BoardViewActivity.this, "새로고침에 실패했습니다.", Toast.LENGTH_SHORT).show();
                            rl_wrapLayout.setRefreshing(false);
                        }
                    });
            }
        });
        boardDataRef.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        refresh();
                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        li_buyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buy();
            }
        });
        lottie_boardView_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                like(lottie_boardView_like, intentBoardItem);
            }
        });
        lottie_boardView_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addEraseCart(lottie_boardView_cart, intentBoardItem);
            }
        });
    }

    private void buy(){
        new AlertDialog.Builder(BoardViewActivity.this)
                .setTitle("구매")
                .setMessage("정말 구매하시겠습니까?")
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        loadingDialog.show();
                        database.getReference("Users").child(mAuth.getCurrentUser().getUid()).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                            @Override
                            public void onSuccess(DataSnapshot dataSnapshot) {
                                User mUser = dataSnapshot.getValue(User.class);
                                Log.d("얼마를 들고있니?1", String.valueOf(mUser.getCoin()));
                                if(intentBoardItem.getPrice() > mUser.getCoin()){
                                    Toast.makeText(BoardViewActivity.this, "코인이 부족합니다. 충전 후 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                else{
                                    mUser.setCoin(mUser.getCoin() - intentBoardItem.getPrice());
                                    dataSnapshot.getRef().setValue(mUser).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            database.getReference("Sales").child(intentBoardItem.getBoardID()).runTransaction(new Transaction.Handler() {
                                                @NonNull
                                                @Override
                                                public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                                                    if (currentData == null) {
                                                        return Transaction.success(currentData);
                                                    }
                                                    currentData.child(mAuth.getCurrentUser().getUid()).setValue(intentBoardItem.getPrice());
                                                    return Transaction.success(currentData);
                                                }

                                                @Override
                                                public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {
                                                    database.getReference("Users").child(intentBoardItem.getUid()).get()
                                                            .addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                                                                @Override
                                                                public void onSuccess(DataSnapshot dataSnapshot) {
                                                                    User seller = dataSnapshot.getValue(User.class);
                                                                    seller.setCoin(seller.getCoin() + intentBoardItem.getPrice());
                                                                    dataSnapshot.getRef().setValue(seller).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                        @Override
                                                                        public void onSuccess(Void unused) {
                                                                            loadingDialog.dismiss();
                                                                            Toast.makeText(BoardViewActivity.this, "구매되었습니다.", Toast.LENGTH_SHORT).show();
                                                                            refresh();
                                                                        }
                                                                    });
                                                                }
                                                            });
                                                }
                                            });
                                        }
                                    });
                                }
                            }
                        });

                    }
                }).setNegativeButton("취소", null).show();
    }

    void buyCheck(){
        //1. 내 아이템일경우,
        if(mAuth.getCurrentUser().getUid().equals(intentBoardItem.getUid())){
            li_boardView_profile.setEnabled(false);
            iv_boardView_userName.setText("내 판매 아이템");
            li_buyBtn.setEnabled(false);
            li_buyBtn.setBackground(getDrawable(R.drawable.border_layout_round_disable_buy));
            lottie_boardView_price.setVisibility(View.GONE);
        }
        else{
            iv_boardView_userName.setText(intentBoardItem.getUsername());
        }

        //2. 내가 가장 많은 돈을 지불했을 때
        if(mAuth.getCurrentUser().getUid().equals(intentBoardItem.getBuyerUid()))
        {
            li_buyBtn.setBackground(getDrawable(R.drawable.border_layout_round_buyeris_me));
        }


        //이미 샀는지 체크
        database.getReference("Sales").child(intentBoardItem.getBoardID()).get()
                .addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                    @Override
                    public void onSuccess(DataSnapshot dataSnapshot) {
                        boolean isBought = false;
                        for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                            if(snapshot.getKey().equals(mAuth.getCurrentUser().getUid())){
                                isBought = true;
                                break;
                            }
                        }
                        if(isBought){
                            //이미 샀다면 실행
                            tv_boardView_price.setText("Download");
                            tv_boardView_price.setTextColor(Color.WHITE);
                            lottie_boardView_price.setVisibility(View.GONE);
                            li_buyBtn.setBackgroundResource(R.drawable.border_layout_round_buyeris_me);
                            li_buyBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    TedPermission.create()
                                            .setPermissionListener(new PermissionListener() {
                                                @Override
                                                public void onPermissionGranted() {
                                                    Intent intent = new Intent(BoardViewActivity.this, FileDownloadActivity.class);
                                                    intent.putExtra("BoardID", intentBoardItem.getBoardID());
                                                    startActivity(intent);
                                                    overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                                                }

                                                @Override
                                                public void onPermissionDenied(List<String> deniedPermissions) {
                                                    Toast.makeText(BoardViewActivity.this, "권한이 없으면 파일을 다운로드할 수 없습니다.", Toast.LENGTH_SHORT).show();
                                                    return;
                                                }
                                            })
                                            .setPermissions(
                                                    Manifest.permission.READ_PHONE_STATE,
                                                    Manifest.permission.READ_EXTERNAL_STORAGE,
                                                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                                            )
                                            .check();
                                }
                            });
                        }
                        else{
                            //사지 않았다면 실행
                            //살 수 있는지 없는지 체크.
                            database.getReference("Users").child(mAuth.getCurrentUser().getUid()).get()
                                    .addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                                            if(task.isSuccessful()){
                                                User mUser = task.getResult().getValue(User.class);
                                                if(mUser.getCoin() < intentBoardItem.getPrice()){
                                                    tv_boardView_price.setTextColor(Color.RED);
                                                }
                                                else{
                                                    tv_boardView_price.setTextColor(Color.WHITE);
                                                }
                                            }
                                            try{
                                                startCountAnimation(Integer.parseInt(tv_boardView_price.getText().toString()), intentBoardItem.getPrice());
                                            }
                                            catch (Exception e){
                                                startCountAnimation(0, intentBoardItem.getPrice());
                                            }
                                        }
                                    });
                        }


                        //여기 해야해 경매처리
                        if(!intentBoardItem.getIsAuction()){
                            lottie_boardView_price.setVisibility(View.VISIBLE);
                        }
                        else{
                            lottie_boardView_price.setVisibility(View.GONE);
                        }
                    }
                });
    }

    void setViews(){
        boardDataRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(task.isSuccessful()){
                    Log.d("보드 키", task.getResult().getKey());
                    BoardItem item = task.getResult().getValue(BoardItem.class);
                    if(item != null){
                        intentBoardItem = null;
                        intentBoardItem = item;
                        Log.d("업데이트 된 가격", Integer.toString(intentBoardItem.getPrice()));
                    }
                    tv_boardView_title.setText(item.getTitle());
                    tv_boardView_body.setText(item.getBody());


                    buyCheck();
                }
            }
        });
    }


    void getBoardImg(){
        StorageReference storageRef = storage.getReference();
        storageRef.child("Board/"+intentBoardItem.getBoardID()+"/")
                .listAll()
                .addOnSuccessListener(new OnSuccessListener<ListResult>() {
                    @Override
                    public void onSuccess(ListResult listResult) {
                        boardImgRefs.clear();
                        for(StorageReference item : listResult.getItems()){
                            boardImgRefs.add(item);
                            Log.d("이미지 레퍼런스", item.getPath());
                        }
                        rv_boardViewImg.setLayoutManager(new LinearLayoutManager(BoardViewActivity.this, RecyclerView.HORIZONTAL, false));
                        pictureRecyclerAdapter.notifyDataSetChanged();
                        loadingDialog.dismiss();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(BoardViewActivity.this, "이미지를 불러오지 못했습니다.", Toast.LENGTH_SHORT).show();
                loadingDialog.dismiss();
            }
        });
    }

    void refresh(){
        setViews();
        getBoardImg();
    }

    private void startCountAnimation(int start, int end) {
        ValueAnimator animator = ValueAnimator.ofInt(start, end); //0 is min number, 600 is max number
        animator.setDuration(2000); //Duration is in milliseconds
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
            }
        });
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                tv_boardView_price.setText(animation.getAnimatedValue().toString());
            }
        });
        animator.start();
    }

    //장바구니 & 좋아요
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
                                Toast.makeText(BoardViewActivity.this, "장바구니에 추가되었습니다.", Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(BoardViewActivity.this, "장바구니에서 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                    else{
                        map.put(item.getBoardID(), true);
                        databaseRef.setValue(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                cartAnim(lottie, true);
                                Toast.makeText(BoardViewActivity.this, "장바구니에 추가되었습니다.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }

                return;
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
        if(isAdded) animator = ValueAnimator.ofFloat(0f, 0.25f).setDuration(500);
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
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.slide_enter, R.anim.slide_exit);
    }
}