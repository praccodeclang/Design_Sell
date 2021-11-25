package com.taewon.shoppingmall.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Debug;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.taewon.shoppingmall.R;
import com.taewon.shoppingmall.User;
import com.taewon.shoppingmall.adapter.AdsViewPagerAdapter;
import com.taewon.shoppingmall.adapter.BoardRecyclerAdapter;
import com.taewon.shoppingmall.adapter.NewestRecyclerAdapter;
import com.taewon.shoppingmall.item.AdsItem;
import com.taewon.shoppingmall.item.BoardItem;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseDatabase database;
    FirebaseStorage storage;
    DrawerLayout drawerLayout;
    EditText et_search;
    LinearLayout wrap_layout;
    BottomNavigationView bottomNavigationView;
    //topDesigner
    TableRow tr_topRatedDesigners;

    //ads
    ViewPager2 vp_adsViewPager;
    AdsViewPagerAdapter pagerAdapter;
    Timer adsTimer;
    ArrayList<AdsItem> adsItems;
    int currentAdsPage = 0;

    //leftDrawer
    LinearLayout li_2d_detail_category;
    LinearLayout li_3d_detail_category;
    LinearLayout li_plan_detail_category;
    ImageView iv_2dDetail;
    ImageView iv_3dDetail;
    ImageView iv_planDetail;


    ArrayList<BoardItem> boardItems;
    //Newest2D
    RecyclerView rv_newest2D;
    BoardRecyclerAdapter boardRecyclerAdapter2D;

    //Newest3D
    RecyclerView rv_newest3D;
    BoardRecyclerAdapter boardRecyclerAdapter3D;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
        adsItems = new ArrayList<>();
        boardItems = new ArrayList<>();
        initViews();
        initListeners();
        initAds();
//        setTodayDesigner();
        setNewest();
    }
    private void initAds(){
        pagerAdapter = new AdsViewPagerAdapter(MainActivity.this, adsItems);
        vp_adsViewPager.setAdapter(pagerAdapter);

        DatabaseReference dataRef = database.getReference();
        dataRef.child("Ads").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(!task.isSuccessful()){
                    Toast.makeText(MainActivity.this, "로딩 오류", Toast.LENGTH_SHORT).show();
                }
                else{
                    Log.d("FireBase", String.valueOf(task.getResult().getValue()));
                    Map<String, Object> map = (Map<String, Object>)task.getResult().getValue();
                    for(String keys : map.keySet()){
                        Map<String, String> child = (Map<String, String>) map.get(keys);
//                        Log.d("image", String.valueOf(child.get("image")));
                        StorageReference storageRef = storage.getReference(child.get("image"));
                        Log.d("DownloadUrl", storageRef.getPath().toString());
                        AdsItem item = new AdsItem(storageRef.getPath(), child.get("ref"));
                        adsItems.add(item);
                    }
                    pagerAdapter.notifyDataSetChanged();
                    Handler handler = new Handler();
                    Runnable Update = new Runnable() {
                        @Override
                        public void run() {
                            if(currentAdsPage == (adsItems.size())){
                                currentAdsPage = 0;
                            }
                            vp_adsViewPager.setCurrentItem(currentAdsPage++, true);
                        }
                    };
                    adsTimer = new Timer();
                    adsTimer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            handler.post(Update);
                        }
                    }, 500, 3000);
                }
            }
        });
    }

    private void initViews(){
        tr_topRatedDesigners = findViewById(R.id.tr_topRatedDesigners);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        et_search = findViewById(R.id.et_search);
        et_search.clearFocus();
        wrap_layout = findViewById(R.id.wrap_layout);
        bottomNavigationView = findViewById(R.id.bottomnav_bottom_menu);
        vp_adsViewPager = findViewById(R.id.vp_adsViewPager);
        li_2d_detail_category = findViewById(R.id.li_2d_detail_category);
        li_3d_detail_category = findViewById(R.id.li_3d_detail_category);
        li_plan_detail_category = findViewById(R.id.li_plan_detail_category);
        iv_2dDetail = findViewById(R.id.iv_2dDetail);
        iv_3dDetail = findViewById(R.id.iv_3dDetail);
        iv_planDetail = findViewById(R.id.iv_planDetail);

        li_2d_detail_category.setVisibility(View.GONE);
        li_3d_detail_category.setVisibility(View.GONE);
        li_plan_detail_category.setVisibility(View.GONE);

        rv_newest2D = findViewById(R.id.rv_newest2D);
        rv_newest3D = findViewById(R.id.rv_newest3D);
    }

    private void setNewest(){
        boardRecyclerAdapter2D = new BoardRecyclerAdapter(MainActivity.this, boardItems);
        rv_newest2D.setAdapter(boardRecyclerAdapter2D);
        DatabaseReference ref = database.getReference("Board/");
        ref.get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    BoardItem item = snapshot.getValue(BoardItem.class);
                    boardItems.add(item);
                }
                GridLayoutManager manager = new GridLayoutManager(MainActivity.this, 3, LinearLayoutManager.VERTICAL, false);
                rv_newest2D.setLayoutManager(manager);
                SnapHelper snapHelper = new LinearSnapHelper();
                snapHelper.attachToRecyclerView(rv_newest2D);
                boardRecyclerAdapter2D.notifyDataSetChanged();
                Log.d("여기까지?", "완료");
            }
        });
    }
    private void setTodayDesigner(){
        DatabaseReference ref = database.getReference("Users");
        ref.get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                ArrayList<User> users = new ArrayList<>();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Log.d("snapshot", snapshot.getValue().toString());
                    User user = snapshot.getValue(User.class);
                    users.add(user);
                }
//                for(User user : users){
//
//                    LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//                    View view = inflater.inflate(R.layout.user_profile_layout, tr_topRatedDesigners, true);
//                    ImageView imageView = view.findViewById(R.id.iv_userProfile);
//                    TextView textView = view.findViewById(R.id.tv_userName);
//                    StorageReference storageRef = storage.getReference(user.getPhotoUrl());
//                    storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//                        @Override
//                        public void onSuccess(Uri uri) {
//                            Glide.with(MainActivity.this)
//                                    .load(uri)
//                                    .override(imageView.getMeasuredWidth(), imageView.getMeasuredHeight())
//                                    .apply(new RequestOptions().circleCrop())
//                                    .into(imageView);
//                            textView.setTextColor(Color.parseColor("#000000"));
//                            textView.setText(user.getUsername());
//                        }
//                    });
//                    //여기부터 시작.
//                }
            }
        });
    }

    private void initListeners(){
        et_search.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    et_search.clearFocus();
                    Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                }
            }
        });
        findViewById(R.id.iv_myinfo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMyInfoDrawer();
            }
        });
        findViewById(R.id.iv_category).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCategoryDrawer();
            }
        });
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.bottom_category:
                        openCategoryDrawer();
                        break;
                    case R.id.bottom_search:
                        Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                        break;
                    case R.id.bottom_home:
                        break;
                    case R.id.bottom_myinfo:
                        openMyInfoDrawer();
                        break;
                    case R.id.bottom_cart:
                        break;
                    default:
                        break;
                }
                return true;
            }
        });

        //왼쪽 드로워 레이아웃
        drawerLayout.findViewById(R.id.iv_leftDrawerCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.closeDrawer(GravityCompat.START);
            }
        });
        drawerLayout.findViewById(R.id.li_2dCategory).setOnClickListener(new View.OnClickListener() {
            //2d 카테고리
            @Override
            public void onClick(View v) {
                if(li_2d_detail_category.getVisibility() == View.GONE){
                    li_2d_detail_category.setVisibility(View.VISIBLE);
                    iv_2dDetail.setImageResource(R.drawable.ic_baseline_arrow_drop_down_24);
                }
                else{
                    li_2d_detail_category.setVisibility(View.GONE);
                    iv_2dDetail.setImageResource(R.drawable.ic_baseline_arrow_right_24);
                }
            }
        });
        drawerLayout.findViewById(R.id.li_3dCategory).setOnClickListener(new View.OnClickListener() {
            //3d 카테고리
            @Override
            public void onClick(View v) {
                if(li_3d_detail_category.getVisibility() == View.GONE){
                    li_3d_detail_category.setVisibility(View.VISIBLE);
                    iv_3dDetail.setImageResource(R.drawable.ic_baseline_arrow_drop_down_24);
                }
                else{
                    li_3d_detail_category.setVisibility(View.GONE);
                    iv_3dDetail.setImageResource(R.drawable.ic_baseline_arrow_right_24);
                }
            }
        });
        drawerLayout.findViewById(R.id.li_plannerCategory).setOnClickListener(new View.OnClickListener() {
            //기획자 카테고리
            @Override
            public void onClick(View v) {
                if(li_plan_detail_category.getVisibility() == View.GONE){
                    li_plan_detail_category.setVisibility(View.VISIBLE);
                    iv_planDetail.setImageResource(R.drawable.ic_baseline_arrow_drop_down_24);
                }
                else{
                    li_plan_detail_category.setVisibility(View.GONE);
                    iv_planDetail.setImageResource(R.drawable.ic_baseline_arrow_right_24);
                }
            }
        });
        drawerLayout.findViewById(R.id.tv_2d_character).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        drawerLayout.findViewById(R.id.tv_2d_background).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        drawerLayout.findViewById(R.id.tv_2d_animation).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        drawerLayout.findViewById(R.id.tv_3d_character).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        drawerLayout.findViewById(R.id.tv_3d_animation).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        drawerLayout.findViewById(R.id.tv_3d_background).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        drawerLayout.findViewById(R.id.tv_3d_modeling).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        drawerLayout.findViewById(R.id.tv_plan_gameIdea).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        drawerLayout.findViewById(R.id.tv_plan_bgMusic).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        drawerLayout.findViewById(R.id.tv_plan_levelDesign).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        drawerLayout.findViewById(R.id.tv_plan_effectSound).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });



        //오른쪽 드로워 레이아웃
        drawerLayout.findViewById(R.id.iv_rightDrawerCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.closeDrawer(GravityCompat.END);
            }
        });
        drawerLayout.findViewById(R.id.li_salesRegistrationBtn).setOnClickListener(new View.OnClickListener() {
            //판매등록
            @Override
            public void onClick(View v) {

            }
        });
        drawerLayout.findViewById(R.id.li_saleItems).setOnClickListener(new View.OnClickListener() {
            //내 판매물품
            @Override
            public void onClick(View v) {

            }
        });
        drawerLayout.findViewById(R.id.li_shoppingBasket).setOnClickListener(new View.OnClickListener() {
            //장바구니
            @Override
            public void onClick(View v) {

            }
        });
        drawerLayout.findViewById(R.id.li_appInfo).setOnClickListener(new View.OnClickListener() {
            //앱정보
            @Override
            public void onClick(View v) {

            }
        });
        drawerLayout.findViewById(R.id.li_serviceCenter).setOnClickListener(new View.OnClickListener() {
            //고객센터
            @Override
            public void onClick(View v) {

            }
        });
        findViewById(R.id.btn_insertTest).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertBoardTest();
            }
        });
    }

    private void openCategoryDrawer(){
        drawerLayout.openDrawer(GravityCompat.START);
        wrap_layout.setClickable(false);
        wrap_layout.setEnabled(false);
    }
    private void openMyInfoDrawer(){
        drawerLayout.openDrawer(GravityCompat.END);
        wrap_layout.setClickable(false);
        wrap_layout.setEnabled(false);
    }

    private void insertBoardTest(){
        DatabaseReference ref = database.getReference("Board/");
        DatabaseReference upload = ref.push();

        StorageReference storageRef = storage.getReference("Board");
        Drawable drawable = getDrawable(R.drawable.example);
        Bitmap bitmap = ((BitmapDrawable)drawable).getBitmap();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, bos);

        storageRef.child(upload.getKey()).putBytes(bos.toByteArray()).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                List<String> imgs = new ArrayList<>();
                List<String> tags = new ArrayList<>();
                imgs.add("Posts/ads1.jpg");
                tags.add("2d");
                tags.add("2d 배경");
                tags.add("2d 디자인");
                BoardItem item = new BoardItem(mAuth.getUid(),"김태원","3번째", "@@@@@@@@", imgs, tags);
                upload.setValue(item);
            }
        });
    }

    //register
//    mAuth.createUserWithEmailAndPassword("rlaxodnjs6574@gmail.com", "dkssud01!")
//            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//        @Override
//        public void onComplete(@NonNull Task<AuthResult> task) {
//            if(task.isSuccessful()){
//                DatabaseReference ref = database.getReference("Users");
//                FirebaseUser user = mAuth.getCurrentUser();
//                User currUser = new User(user.getUid(), user.getEmail());
//                ref.child(user.getUid()).setValue(currUser);
//                Toast.makeText(MainActivity.this, "성공하였습니다.", Toast.LENGTH_SHORT).show();
//            }
//            else{
//                Toast.makeText(MainActivity.this, "실패하였습니다.", Toast.LENGTH_SHORT).show();
//            }
//        }
//    });


}