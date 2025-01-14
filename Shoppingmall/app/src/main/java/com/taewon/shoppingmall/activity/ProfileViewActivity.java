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
import com.taewon.shoppingmall.item.NotifyItem;
import com.taewon.shoppingmall.item.User;
import com.taewon.shoppingmall.adapter.MiniBoardRecyclerAdapter;
import com.taewon.shoppingmall.dialog.LottieLoadingDialog;
import com.taewon.shoppingmall.item.BoardItem;
import com.taewon.shoppingmall.util.BoardDateComparator;
import com.taewon.shoppingmall.util.BoardStarCountComparator;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;

public class ProfileViewActivity extends AppCompatActivity {
    enum FollowState{
        FollowNone,
        FollowSend,
        FollowReceived,
        FollowBoth
    }

    SwipeRefreshLayout swipe_profile_wrap;
    TableRow tr_profile_interaction_layout;
    ImageView iv_userViewerImg;
    TextView tv_userViewerEmail;
    TextView tv_userViewerName;
    TextView tv_profile_followingCount;
    TextView tv_profile_followerCount;

    ImageView iv_profile_follow;
    TextView tv_profile_follow;
    ImageView iv_profile_chat;

    LinearLayout li_profile_info;
    TextView tv_profile_user_phone;

    TextView tv_profile_feed_empty;
    RecyclerView rv_profile_feed;
    FeedRecyclerAdapter feedRecyclerAdapter;

    RecyclerView rv_profile_newest_board;
    MiniBoardRecyclerAdapter newestBoardAdapter;

    LottieLoadingDialog loadingDialog;
    FirebaseDatabase database;
    FirebaseStorage storage;
    FirebaseAuth mAuth;
    DatabaseReference currUserRef;
    DatabaseReference profileUserRef;
    DatabaseReference relationshipRef;


    ArrayList<BoardItem> mBoardItems;
    ArrayList<BoardItem> newerItems;

    User profileUser;
    FollowState followState;

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
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
        mAuth = FirebaseAuth.getInstance();
        profileUser = (User) getIntent().getSerializableExtra("UserData");
        currUserRef = database.getReference("Users").child(mAuth.getCurrentUser().getUid());
        profileUserRef = database.getReference("Users").child(profileUser.getUid());
        relationshipRef = database.getReference("RelationShip");

        loadingDialog = new LottieLoadingDialog(ProfileViewActivity.this);
        mBoardItems = new ArrayList<>();
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
        tv_profile_follow = findViewById(R.id.tv_profile_follow);
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

        if(mAuth.getCurrentUser().getUid().equals(profileUser.getUid())){
            tr_profile_interaction_layout.setVisibility(View.GONE);
            li_profile_info.setVisibility(View.VISIBLE);
        }
    }

    private void initListeners(){
        swipe_profile_wrap.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
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

                // 5. 로딩창 닫기
                loadingDialog.dismiss();
            }
        }).addOnFailureListener(new OnFailureListener() {
            // 연결에 실패했을 때.
            public void onFailure(Exception e) {
                loadingDialog.dismiss();
                new AlertDialog.Builder(ProfileViewActivity.this)
                        .setTitle("오류")
                        .setMessage("게시글을 불러오지 못했습니다.\n다시 시도해보세요.")
                        .setIcon(R.drawable.ic_baseline_back_hand_24)
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        getBoard();
                    }
                }).setNegativeButton("취소", (DialogInterface.OnClickListener) null).create().show();
            }
        });
    }

    private void refresh(){
        followCheck();
        getBoard();
    }

    private void followSend(){
        relationshipRef.child(mAuth.getCurrentUser().getUid())
                .child("following")
                .child(profileUser.getUid())
                .setValue(true)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        relationshipRef.child(profileUser.getUid())
                                .child("follower")
                                .child(mAuth.getCurrentUser().getUid())
                                .setValue(false)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        notifyFollow("팔로우 요청을 보냈습니다.");
                                        refresh();
                                    }
                                });
                    }
                });
    }

    private void followCancel(){
        relationshipRef.child(mAuth.getCurrentUser().getUid())
                .child("following")
                .child(profileUser.getUid())
                .setValue(null)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        relationshipRef.child(profileUser.getUid())
                                .child("follower")
                                .child(mAuth.getCurrentUser().getUid())
                                .setValue(null)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(ProfileViewActivity.this, "팔로우를 취소했습니다.", Toast.LENGTH_SHORT).show();
                                        refresh();
                                    }
                                });
                    }
                });
    }

    private void followAccept(){
        relationshipRef.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                if(currentData == null){
                    return Transaction.abort();
                }
                currentData.child(profileUser.getUid())
                        .child("follower")
                        .child(mAuth.getCurrentUser().getUid())
                        .setValue(true);
                currentData.child(profileUser.getUid())
                        .child("following")
                        .child(mAuth.getCurrentUser().getUid())
                        .setValue(true);
                currentData.child(mAuth.getCurrentUser().getUid())
                        .child("follower")
                        .child(profileUser.getUid())
                        .setValue(true);
                currentData.child(mAuth.getCurrentUser().getUid())
                        .child("following")
                        .child(profileUser.getUid())
                        .setValue(true);
                return Transaction.success(currentData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {
                Toast.makeText(ProfileViewActivity.this, "팔로우를 수락했습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void followCheck(){
        followState = FollowState.FollowNone;
        // 1. 현재 유저가 프로필 유저를 팔로우했는지 체크.
        relationshipRef
                .child(profileUser.getUid())
                .child("follower")
                .child(mAuth.getCurrentUser().getUid())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                    @Override
                    public void onSuccess(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            followState = FollowState.FollowSend;
                            Log.d("FollowCheck", "현재 유저가 프로필 유저에게 팔로우 요청을 보냄.");
                        }

                        relationshipRef
                                .child(profileUser.getUid())
                                .child("following")
                                .child(mAuth.getCurrentUser().getUid())
                                .get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                            @Override
                            public void onSuccess(DataSnapshot dataSnapshot) {
                                if(dataSnapshot.exists()){
                                    if(followState == FollowState.FollowSend){
                                        followState = FollowState.FollowBoth;
                                    }
                                    else{
                                        followState = FollowState.FollowReceived;
                                    }
                                }
                                followStateUpdate();
                            }
                        });
                    }
                });
    }

    private void followStateUpdate(){
        switch (followState){
            case FollowNone:
                iv_profile_follow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        followSend();
                    }
                });
                tv_profile_follow.setText("Follow");
                iv_profile_follow.setImageResource(R.drawable.ic_baseline_person_add_24);
                break;

            case FollowSend:
                iv_profile_follow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        followCancel();
                    }
                });
                tv_profile_follow.setText("Follow Send");
                iv_profile_follow.setImageResource(R.drawable.ic_follow_send);
                break;

            case FollowBoth:
                iv_profile_follow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        followCancel();
                    }
                });
                tv_profile_follow.setText("Follower");
                iv_profile_follow.setImageResource(R.drawable.ic_baseline_person_24);
                break;

            case FollowReceived:
                iv_profile_follow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(ProfileViewActivity.this);
                        builder.setTitle("팔로우")
                                .setMessage("팔로우를 수락하시겠습니까?")
                                .setIcon(R.drawable.ic_baseline_question_mark_24)
                                .setPositiveButton("수락", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        followAccept();
                                    }
                                })
                                .setNegativeButton("거절", null)
                                .show();
                    }
                });
                tv_profile_follow.setText("Follow Received");
                iv_profile_follow.setImageResource(R.drawable.ic_follow_recieved);
                break;
        }
    }




    public void notifyFollow(String message){
        database.getReference("Users").child(mAuth.getCurrentUser().getUid())
                .get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                User currUser = dataSnapshot.getValue(User.class);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = Calendar.getInstance().getTime();
                sdf.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
                NotifyItem notifyItem = new NotifyItem(currUser.getUid(), "팔로우 알림", currUser.getUsername()+"님이 " + message, sdf.format(date), "follow", false);
                database.getReference("Notify").child(profileUser.getUid()).push().setValue(notifyItem);
                Toast.makeText(ProfileViewActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
