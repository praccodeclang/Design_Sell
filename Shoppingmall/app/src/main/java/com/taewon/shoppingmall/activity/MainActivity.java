package com.taewon.shoppingmall.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.taewon.shoppingmall.R;
import com.taewon.shoppingmall.User;
import com.taewon.shoppingmall.adapter.AdsViewPagerAdapter2;
import com.taewon.shoppingmall.adapter.AdsViewPagerAdapter;
import com.taewon.shoppingmall.item.AdsItem;

import java.util.ArrayList;
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

    //ads
    ViewPager2 vp_adsViewPager;
//    AdsViewPagerAdapter pagerAdapter;
    AdsViewPagerAdapter2 pagerAdapter;
    Timer adsTimer;

    //ads2
    ViewFlipper vf_adsFlipper;
    ArrayList<AdsItem> adsItems;

    int currentAdsPage = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
        adsItems = new ArrayList<>();
        initViews();
        initListeners();
        initAds();
    }
    private void initAds(){
//        pagerAdapter = new AdsViewPagerAdapter(this, adsItems);
        pagerAdapter = new AdsViewPagerAdapter2(MainActivity.this, adsItems);
        vp_adsViewPager.setAdapter(pagerAdapter);

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

        //Ads2
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
                }
            }
        });
    }

    private void initViews(){
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        et_search = findViewById(R.id.et_search);
        et_search.clearFocus();
        wrap_layout = findViewById(R.id.wrap_layout);
        bottomNavigationView = findViewById(R.id.bottomnav_bottom_menu);
        vp_adsViewPager = findViewById(R.id.vp_adsViewPager);
        vf_adsFlipper = findViewById(R.id.vf_adsFlipper);
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
        drawerLayout.findViewById(R.id.li_2dCategory).setOnClickListener(new View.OnClickListener() {
            //2d 카테고리
            @Override
            public void onClick(View v) {

            }
        });
        drawerLayout.findViewById(R.id.li_3dCategory).setOnClickListener(new View.OnClickListener() {
            //3d 카테고리
            @Override
            public void onClick(View v) {

            }
        });
        drawerLayout.findViewById(R.id.li_plannerCategory).setOnClickListener(new View.OnClickListener() {
            //기획자 카테고리
            @Override
            public void onClick(View v) {

            }
        });
        //오른쪽 드로워 레이아웃
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