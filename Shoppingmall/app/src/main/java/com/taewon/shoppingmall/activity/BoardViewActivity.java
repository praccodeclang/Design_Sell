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
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager2.widget.ViewPager2;

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
import com.taewon.shoppingmall.adapter.BoardCommentsRecyclerAdapter;
import com.taewon.shoppingmall.dialog.BoardEditDialog;
import com.taewon.shoppingmall.dialog.LottieLoadingDialog;
import com.taewon.shoppingmall.item.BoardCommentItem;
import com.taewon.shoppingmall.item.NotifyItem;
import com.taewon.shoppingmall.item.User;
import com.taewon.shoppingmall.adapter.BoardPictureRecyclerAdapter;
import com.taewon.shoppingmall.item.BoardItem;
import com.taewon.shoppingmall.util.BoardCommentDateComparator;
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

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
    ImageView iv_boardview_etc;

    ViewPager2 rv_boardViewImg;
    DotsIndicator boardview_dots_indicator;
    BoardPictureRecyclerAdapter pictureRecyclerAdapter;
    ArrayList<StorageReference> boardImgRefs;

    RecyclerView rv_board_comments;
    BoardCommentsRecyclerAdapter commentsRecyclerAdapter;
    ArrayList<BoardCommentItem> boardCommentItems;

    LinearLayout li_boardView_profile;
    ImageView iv_boardView_userImg;
    TextView iv_boardView_userName;

    TextView tv_boardView_body;
    LinearLayout li_buyBtn;
    LottieAnimationView lottie_boardView_price;
    LottieAnimationView lottie_boardView_like;
    LottieAnimationView lottie_boardView_cart;
    TextView tv_boardView_price;

    EditText et_board_comment;
    ImageButton ib_board_comment_send;




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
            e.printStackTrace();
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
        boardCommentItems = new ArrayList<>();

        intent = getIntent();
        intentBoardItem = (BoardItem) intent.getSerializableExtra("BoardItem");
        boardDataRef = database.getReference().child("Board").child(intentBoardItem.getBoardID());
    }

    void initViews(){
        rl_wrapLayout = findViewById(R.id.rl_wrapLayout);
        tv_boardView_title = findViewById(R.id.tv_boardView_title);
        iv_boardview_etc = findViewById(R.id.iv_boardview_etc);

        //게시글 이미지 리사이클러뷰 레이아웃 초기화 및 어댑터
        rv_boardViewImg = findViewById(R.id.rv_boardViewImg);
        boardview_dots_indicator = findViewById(R.id.boardview_dots_indicator);
        pictureRecyclerAdapter = new BoardPictureRecyclerAdapter(BoardViewActivity.this, boardImgRefs);
        rv_boardViewImg.setAdapter(pictureRecyclerAdapter);
        boardview_dots_indicator.setViewPager2(rv_boardViewImg);

        //게시글 댓글 리사이클러뷰 레이아웃 초기화 및 어댑터
        rv_board_comments = findViewById(R.id.rv_board_comments);
        commentsRecyclerAdapter = new BoardCommentsRecyclerAdapter(BoardViewActivity.this, boardCommentItems);
        rv_board_comments.setAdapter(commentsRecyclerAdapter);
        rv_board_comments.setLayoutManager(new LinearLayoutManager(BoardViewActivity.this));
        new LinearSnapHelper().attachToRecyclerView(rv_board_comments);

        li_boardView_profile = findViewById(R.id.li_boardView_profile);
        iv_boardView_userImg = findViewById(R.id.iv_boardView_userImg);
        iv_boardView_userName = findViewById(R.id.iv_boardView_userName);
        tv_boardView_body = findViewById(R.id.tv_boardView_body);
        lottie_boardView_price = findViewById(R.id.lottie_boardView_price);
        lottie_boardView_like = findViewById(R.id.lottie_boardView_like);
        lottie_boardView_cart = findViewById(R.id.lottie_boardView_cart);
        li_buyBtn = findViewById(R.id.li_buyBtn);
        tv_boardView_price = findViewById(R.id.tv_boardView_price);
        et_board_comment = findViewById(R.id.et_board_comment);
        ib_board_comment_send = findViewById(R.id.ib_board_comment_send);

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
        iv_boardview_etc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new BoardEditDialog(BoardViewActivity.this, intentBoardItem, new BoardEditDialog.DialogClickListener() {
                    @Override
                    public void onDelete(String result) {
                        if(result.equals("OK")){
                            finish();
                            overridePendingTransition(R.anim.slide_enter, R.anim.slide_exit);
                        }
                    }
                }).show();
            }
        });
        ib_board_comment_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                writeComment();
            }
        });
    }
    void setViews(){
        boardCommentItems.clear();
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
                    try {
                        for(String key : item.getComments().keySet()){
                            HashMap<String, Object> obj = (HashMap<String, Object>) item.getComments().get(key);
                            String uid = obj.get("uid").toString();
                            String username = obj.get("username").toString();
                            String comment = obj.get("comment").toString();
                            String dateString = obj.get("dateString").toString();
                            BoardCommentItem commentItem = new BoardCommentItem(uid, username, comment, dateString);
                            commentItem.setBoardID(intentBoardItem.getBoardID());
                            commentItem.setCommentID(obj.get("commentID").toString());
                            boardCommentItems.add(commentItem);
                        }
                        Collections.sort(boardCommentItems, new BoardCommentDateComparator(true));
                        commentsRecyclerAdapter.notifyDataSetChanged();
                    }
                    catch (Exception e){
                        e.printStackTrace();
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
        getBoardImg();
        setViews();
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
                                                                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                                                            Date date = Calendar.getInstance().getTime();
                                                                            sdf.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
                                                                            NotifyItem notifyItem = new NotifyItem(
                                                                                    mUser.getUid(),
                                                                                    "구매 알림",
                                                                                    mUser.getUsername() +"님이 \"" + intentBoardItem.getTitle() +"\" 아이템을 구매했습니다.",
                                                                                    sdf.format(date),
                                                                                    false
                                                                            );
                                                                            database.getReference("Notify").child(intentBoardItem.getUid()).push().setValue(notifyItem);
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


    /* --------------------------------------- 장바구니 & 좋아요 -------------------------- */
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
    /*-------------------장바구니 & 좋아요 End */
    /* Custom Function */
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

    //댓글 작성
    private void writeComment(){
        String commentString = et_board_comment.getText().toString();
        if(commentString.isEmpty() || commentString.trim().equals("")){
            Toast.makeText(BoardViewActivity.this, "댓글을 입력해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }
        database.getReference("Users").child(mAuth.getCurrentUser().getUid()).get()
                .addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                    @Override
                    public void onSuccess(DataSnapshot dataSnapshot) {
                        User currUser = dataSnapshot.getValue(User.class);
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        Date date = Calendar.getInstance().getTime();
                        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
                        BoardCommentItem commentItem = new BoardCommentItem(currUser.getUid(), currUser.getUsername(), commentString, sdf.format(date));
                        DatabaseReference ref = database.getReference("Board").child(intentBoardItem.getBoardID()).child("comments").push();
                        commentItem.setCommentID(ref.getKey());
                        commentItem.setBoardID(intentBoardItem.getBoardID());

                        ref.setValue(commentItem).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(BoardViewActivity.this, "작성되었습니다.", Toast.LENGTH_SHORT).show();
                                et_board_comment.setText("");
                                refresh();
                            }
                        });
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