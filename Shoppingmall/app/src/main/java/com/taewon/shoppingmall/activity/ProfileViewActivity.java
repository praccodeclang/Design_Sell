package com.taewon.shoppingmall.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.taewon.shoppingmall.R;
import com.taewon.shoppingmall.User;
import com.taewon.shoppingmall.adapter.BoardRecyclerAdapter;
import com.taewon.shoppingmall.adapter.MiniBoardRecyclerAdapter;
import com.taewon.shoppingmall.dialog.LottieLoadingDialog;
import com.taewon.shoppingmall.item.BoardItem;
import com.taewon.shoppingmall.util.BoardDateComparator;
import com.taewon.shoppingmall.util.BoardStarCountComparator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class ProfileViewActivity extends AppCompatActivity {

    ImageView iv_userViewerImg;
    TextView tv_userViewerEmail;
    TextView tv_userViewerName;

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

    User user;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_viewer);
        init();
        initViews();
        initListeners();
        getBoard();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void init(){
        user = (User) getIntent().getSerializableExtra("UserData");
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
        mAuth = FirebaseAuth.getInstance();
        loadingDialog = new LottieLoadingDialog(ProfileViewActivity.this);
        mBoardItems = new ArrayList<>();
        popularItems = new ArrayList<>();
        newerItems = new ArrayList<>();
    }

    private void initViews(){
        iv_userViewerImg = findViewById(R.id.iv_userViewerImg);
        storage.getReference(user.getPhotoUrl()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(ProfileViewActivity.this)
                        .load(uri)
                        .error(R.drawable.ic_warning)
                        .into(iv_userViewerImg);
            }
        });
        tv_userViewerEmail = findViewById(R.id.tv_userViewerEmail);
        tv_userViewerEmail.setText(user.getEmail());
        tv_userViewerName = findViewById(R.id.tv_userViewerName);
        tv_userViewerName.setText(user.getUsername());

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

    }

    private void getBoard() {
        loadingDialog.show();
        database.getReference("Board/").get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            public void onSuccess(DataSnapshot dataSnapshot) {
                // 1.데이터는 쌓인다. 청소하자.
                mBoardItems.clear();
                popularItems.clear();
                newerItems.clear();
                // 2. 가져온 보드들을 우선 모두 리스트에 정리하고,
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    BoardItem item = (BoardItem) snapshot.getValue(BoardItem.class);
                    item.setBoardID(snapshot.getKey());
                    if(item.getUid().equals(user.getUid())){
                        Log.d("UID",item.getUid());
                        mBoardItems.add(item);
                    }
                }

                // 4. 아이템들을 최신순, 인기순으로 정렬.
                // 5. 어댑터에 리스트 데이터 구조가 바뀌었음을 알려주자.
                Collections.sort(mBoardItems, new BoardDateComparator());
                newerItems.addAll(mBoardItems);
                newestBoardAdapter.notifyDataSetChanged();

                Collections.sort(mBoardItems, new BoardStarCountComparator());
                popularItems.addAll(mBoardItems);
                popularBoardAdapter.notifyDataSetChanged();
                // 6. 로딩창 닫기
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

}
