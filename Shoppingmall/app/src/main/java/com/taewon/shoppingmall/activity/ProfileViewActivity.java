package com.taewon.shoppingmall.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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
import com.google.firebase.storage.FirebaseStorage;
import com.taewon.shoppingmall.R;
import com.taewon.shoppingmall.adapter.FeedRecyclerAdapter;
import com.taewon.shoppingmall.item.User;
import com.taewon.shoppingmall.adapter.MiniBoardRecyclerAdapter;
import com.taewon.shoppingmall.dialog.LottieLoadingDialog;
import com.taewon.shoppingmall.item.BoardItem;
import com.taewon.shoppingmall.util.BoardDateComparator;
import com.taewon.shoppingmall.util.BoardStarCountComparator;

import java.util.ArrayList;
import java.util.Collections;

public class ProfileViewActivity extends AppCompatActivity {

    SwipeRefreshLayout swipe_profile_wrap;
    TableRow tr_profile_interaction_layout;
    ImageView iv_userViewerImg;
    TextView tv_userViewerEmail;
    TextView tv_userViewerName;
    TextView tv_profile_followingCount;
    TextView tv_profile_followerCount;

    ImageView iv_profile_follow;
    ImageView iv_profile_chat;

    LinearLayout li_profile_info;
    TextView tv_profile_user_phone;

    TextView tv_profile_feed_empty;
    RecyclerView rv_profile_feed;
    FeedRecyclerAdapter feedRecyclerAdapter;

    RecyclerView rv_profile_newest_board;
    MiniBoardRecyclerAdapter newestBoardAdapter;

    RecyclerView rv_profile_popular_board;
    MiniBoardRecyclerAdapter popularBoardAdapter;

    LottieLoadingDialog loadingDialog;
    FirebaseDatabase database;
    FirebaseStorage storage;
    FirebaseAuth mAuth;
    ArrayList<BoardItem> mBoardItems;
    ArrayList<BoardItem> popularItems;
    ArrayList<BoardItem> newerItems;

    User profileUser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_viewer);
        init();
        initViews();
        initListeners();
    }

    @Override
    protected void onStart() {
        super.onStart();
        refresh();
    }

    private void init(){
        profileUser = (User) getIntent().getSerializableExtra("UserData");
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
        mAuth = FirebaseAuth.getInstance();
        loadingDialog = new LottieLoadingDialog(ProfileViewActivity.this);
        mBoardItems = new ArrayList<>();
        popularItems = new ArrayList<>();
        newerItems = new ArrayList<>();
    }

    private void initViews(){
        li_profile_info = findViewById(R.id.li_profile_info);
        li_profile_info.setVisibility(View.GONE);
        tv_profile_user_phone = findViewById(R.id.tv_profile_user_phone);
        swipe_profile_wrap = findViewById(R.id.swipe_profile_wrap);
        tv_profile_user_phone.setText(profileUser.getPhone());
        tr_profile_interaction_layout = findViewById(R.id.tr_profile_interaction_layout);
        iv_profile_follow = findViewById(R.id.iv_profile_follow);
        iv_profile_chat = findViewById(R.id.iv_profile_chat);
        tv_profile_followingCount = findViewById(R.id.tv_profile_followingCount);
        tv_profile_followerCount = findViewById(R.id.tv_profile_followerCount);
        tv_profile_feed_empty = findViewById(R.id.tv_profile_feed_empty);

        iv_userViewerImg = findViewById(R.id.iv_userViewerImg);
        storage.getReference(profileUser.getPhotoUrl()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(ProfileViewActivity.this)
                        .load(uri)
                        .apply(new RequestOptions().circleCrop())
                        .error(R.drawable.ic_warning)
                        .into(iv_userViewerImg);
            }
        });
        tv_userViewerEmail = findViewById(R.id.tv_userViewerEmail);
        tv_userViewerEmail.setText(profileUser.getEmail());
        tv_userViewerName = findViewById(R.id.tv_userViewerName);
        tv_userViewerName.setText(profileUser.getUsername());

        SnapHelper helper = new LinearSnapHelper();
        rv_profile_feed = findViewById(R.id.rv_profile_feed);
        feedRecyclerAdapter = new FeedRecyclerAdapter(ProfileViewActivity.this, mBoardItems);
        rv_profile_feed.setLayoutManager(new GridLayoutManager(ProfileViewActivity.this, 3));
        rv_profile_feed.setAdapter(feedRecyclerAdapter);
        helper.attachToRecyclerView(rv_profile_feed);


        rv_profile_newest_board = findViewById(R.id.rv_profile_newest_board);
        newestBoardAdapter = new MiniBoardRecyclerAdapter(ProfileViewActivity.this, newerItems);
        rv_profile_newest_board.setLayoutManager(new LinearLayoutManager(ProfileViewActivity.this, RecyclerView.VERTICAL, false));
        rv_profile_newest_board.setAdapter(newestBoardAdapter);

        rv_profile_popular_board = findViewById(R.id.rv_profile_popular_board);
        popularBoardAdapter = new MiniBoardRecyclerAdapter(ProfileViewActivity.this, popularItems);
        rv_profile_popular_board.setLayoutManager(new LinearLayoutManager(ProfileViewActivity.this, RecyclerView.VERTICAL, false));
        rv_profile_popular_board.setAdapter(popularBoardAdapter);
    }

    private void initListeners(){
        swipe_profile_wrap.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipe_profile_wrap.setRefreshing(true);
                refresh();
                swipe_profile_wrap.setRefreshing(false);
            }
        });
    }

    private void getBoard() {
        loadingDialog.show();
        database.getReference("Board/").get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            public void onSuccess(DataSnapshot dataSnapshot) {
                // 1.데이터는 쌓인다. 청소하자.
                mBoardItems.clear();
                popularItems.clear();
                newerItems.clear();
                // 2. 가져온 전체 보드 중, 현재 유저 보드만 정리.
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    BoardItem item = (BoardItem) snapshot.getValue(BoardItem.class);
                    item.setBoardID(snapshot.getKey());
                    if(item.getUid().equals(profileUser.getUid())){
                        Log.d("UID",item.getUid());
                        mBoardItems.add(item);
                    }
                }
                // 3. 피드생성.
                if(mBoardItems.size() < 1){
                    tv_profile_feed_empty.setVisibility(View.VISIBLE);
                }
                else{
                    tv_profile_feed_empty.setVisibility(View.GONE);
                }
                feedRecyclerAdapter.notifyDataSetChanged();

                // 4. 아이템들을 최신순, 인기순으로 정렬.
                Collections.sort(mBoardItems, new BoardDateComparator());
                for(int i = 0; i < mBoardItems.size(); i++){
                    if(i > 2) break;
                    newerItems.add(mBoardItems.get(i));
                }
                newestBoardAdapter.notifyDataSetChanged();

                Collections.sort(mBoardItems, new BoardStarCountComparator());
                for(int i = 0; i < mBoardItems.size(); i++){
                    if(i > 2) break;
                    popularItems.add(mBoardItems.get(i));
                }
                popularBoardAdapter.notifyDataSetChanged();

                // 5. 로딩창 닫기
                loadingDialog.dismiss();
            }
        }).addOnFailureListener(new OnFailureListener() {
            // 연결에 실패했을 때.
            public void onFailure(Exception e) {
                loadingDialog.dismiss();
                new AlertDialog.Builder(ProfileViewActivity.this).setTitle("게시글을 불러오지 못했습니다. 다시 시도해보세요.").setMessage("").setIcon(R.drawable.ic_baseline_back_hand_24).setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        getBoard();
                    }
                }).setNegativeButton("취소", (DialogInterface.OnClickListener) null).create().show();
            }
        });
    }

    private void refresh(){
        database.getReference().child("Users").child(mAuth.getCurrentUser().getUid()).get()
                .addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if(task.isSuccessful()){
                            //현재 사용자
                            User user = task.getResult().getValue(User.class);
                            if(user.getUid().equals(profileUser.getUid())){
                                //내 프로필이면?
                                tr_profile_interaction_layout.setVisibility(View.GONE);
                                li_profile_info.setVisibility(View.VISIBLE);
                            }
                            followCheck(profileUser);
                        }
                    }
                });
        getBoard();
    }

    private void followCheck(User user){
        iv_profile_follow.setImageResource(R.drawable.ic_baseline_person_add_24);
        iv_profile_follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                follow();
            }
        });
        li_profile_info.setVisibility(View.GONE);

        DatabaseReference followersDataRef = database.getReference("Followers").child(user.getUid());
        followersDataRef.get()
                .addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if(task.isSuccessful()){
                            int i = 0;
                            for(DataSnapshot snapshot : task.getResult().getChildren()){
                                if(snapshot.getKey().equals(mAuth.getCurrentUser().getUid())){
                                    iv_profile_follow.setImageResource(R.drawable.ic_baseline_check_24);
                                    li_profile_info.setVisibility(View.VISIBLE);
                                    iv_profile_follow.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            unfollow();
                                        }
                                    });
                                }
                                i++;
                            }
                            tv_profile_followerCount.setText(Integer.toString(i));
                        }
                    }
                });
        DatabaseReference followingsDataRef = database.getReference("Followings").child(user.getUid());
        followingsDataRef.get()
                .addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if(task.isSuccessful()){
                            int i=0;
                            for(DataSnapshot snapshot : task.getResult().getChildren()){
                                i++;
                            }
                            tv_profile_followingCount.setText(Integer.toString(i));
                        }
                    }
                });
    }


    private void follow(){
        database.getReference("Following").child(mAuth.getCurrentUser().getUid()).runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                if(currentData == null){
                    return Transaction.success(currentData);
                }
                currentData.child(profileUser.getUid()).setValue(true);
                return  Transaction.success(currentData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {

            }
        });
        database.getReference("Followers").child(profileUser.getUid()).runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                if(currentData == null){
                    return Transaction.success(currentData);
                }
                currentData.child(mAuth.getCurrentUser().getUid()).setValue(true);
                return  Transaction.success(currentData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {
                refresh();
            }
        });
    }

    private void unfollow(){
        Log.d("언팔", "언팔");
        AlertDialog.Builder builder = new AlertDialog.Builder(ProfileViewActivity.this);
        builder.setTitle("팔로우 끊기")
                .setIcon(R.drawable.ic_baseline_back_hand_24)
                .setMessage("정말로 팔로우를 끊으시겠습니까?")
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        database.getReference("Following").child(mAuth.getCurrentUser().getUid()).runTransaction(new Transaction.Handler() {
                            @NonNull
                            @Override
                            public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                                if(currentData == null){
                                    return Transaction.success(currentData);
                                }
                                currentData.child(profileUser.getUid()).setValue(null);
                                return  Transaction.success(currentData);
                            }
                            @Override
                            public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {
                                database.getReference("Followers").child(profileUser.getUid()).runTransaction(new Transaction.Handler() {
                                    @NonNull
                                    @Override
                                    public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                                        if(currentData == null){
                                            return Transaction.success(currentData);
                                        }
                                        currentData.child(mAuth.getCurrentUser().getUid()).setValue(null);
                                        return  Transaction.success(currentData);
                                    }

                                    @Override
                                    public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {
                                        refresh();
                                    }
                                });
                            }
                        });

                    }
                })
                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();
    }
}
