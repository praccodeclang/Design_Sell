package com.taewon.shoppingmall.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.taewon.shoppingmall.R;
import com.taewon.shoppingmall.dialog.LottieLoadingDialog;
import com.taewon.shoppingmall.item.User;
import com.taewon.shoppingmall.adapter.BoardPictureRecyclerAdapter;
import com.taewon.shoppingmall.item.BoardItem;

import java.util.ArrayList;

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
    LottieAnimationView lottie_boardView_auction;
    LottieAnimationView lottie_boardView_price;
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
            refreshBoard();
        }catch (Exception e){
            loadingDialog.dismiss();
            Toast.makeText(BoardViewActivity.this, "게시글을 불러오지 못했습니다.", Toast.LENGTH_SHORT).show();
            onBackPressed();
        }
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
        lottie_boardView_auction = findViewById(R.id.lottie_boardView_auction);
        lottie_boardView_price = findViewById(R.id.lottie_boardView_price);
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
                                refreshBoard();
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
                        refreshBoard();
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
//                if()
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

                    //1. 내 아이템일경우,
                    if(mAuth.getCurrentUser().getUid().equals(item.getUid())){
                        li_boardView_profile.setEnabled(false);
                        iv_boardView_userName.setText("내 판매 아이템");
                        li_buyBtn.setEnabled(false);
                        li_buyBtn.setBackground(getDrawable(R.drawable.border_layout_round_disable_buy));
                    }
                    else{
                        iv_boardView_userName.setText(item.getUsername());
                    }

                    //2. 내가 가장 많은 돈을 지불했을 때
                    if(mAuth.getCurrentUser().getUid().equals(item.getBuyerUid()))
                    {
                        li_buyBtn.setEnabled(false);
                        li_buyBtn.setBackground(getDrawable(R.drawable.border_layout_round_buyeris_me));
                    }

                    try{
                        startCountAnimation(Integer.parseInt(tv_boardView_price.getText().toString()), item.getPrice());
                    }
                    catch (Exception e){
                        startCountAnimation(0, item.getPrice());
                    }
//                    tv_boardView_price.setText(Integer.toString(item.getPrice()));


                    //경매면 망치, 아니면 코인
                    if(!item.getIsAuction()){
                        lottie_boardView_auction.setVisibility(View.GONE);
                        lottie_boardView_price.setVisibility(View.VISIBLE);
                    }
                    else{
                        lottie_boardView_auction.setVisibility(View.VISIBLE);
                        lottie_boardView_price.setVisibility(View.GONE);
                    }
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

    void refreshBoard(){
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.slide_enter, R.anim.slide_exit);
    }
}