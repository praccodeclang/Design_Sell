package com.taewon.shoppingmall.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
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
import com.taewon.shoppingmall.dialog.LottieLoadingDialog;
import com.taewon.shoppingmall.item.AdsItem;
import com.taewon.shoppingmall.item.BoardItem;
import com.taewon.shoppingmall.util.BoardDateComparator;
import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    ArrayList<AdsItem> adsItems;
    Timer adsTimer;
    ArrayList<BoardItem> boardItems;
    BoardRecyclerAdapter boardRecyclerAdapter2D;
    BoardRecyclerAdapter boardRecyclerAdapter3D;
    BottomNavigationView bottomNavigationView;
    int currentAdsPage = 0;
    FirebaseDatabase database;
    DrawerLayout drawerLayout;
    EditText et_search;
    ArrayList<BoardItem> items2D;
    ArrayList<BoardItem> items3D;
    ImageView iv_2dDetail;
    ImageView iv_3dDetail;
    ImageView iv_planDetail;
    LinearLayout li_2d_detail_category;
    LinearLayout li_3d_detail_category;
    LinearLayout li_plan_detail_category;
    LottieLoadingDialog loadingDialog;
    FirebaseAuth mAuth;
    AdsViewPagerAdapter pagerAdapter;
    RecyclerView rv_newest2D;
    RecyclerView rv_newest3D;
    FirebaseStorage storage;
    TableRow tr_topRatedDesigners;
    ViewPager2 vp_adsViewPager;
    LinearLayout wrap_layout;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_main);
        this.mAuth = FirebaseAuth.getInstance();
        this.database = FirebaseDatabase.getInstance();
        this.storage = FirebaseStorage.getInstance();
        this.adsItems = new ArrayList<>();
        this.boardItems = new ArrayList<>();
        this.items2D = new ArrayList<>();
        this.items3D = new ArrayList<>();
        this.items2D = new ArrayList<>();
        this.items3D = new ArrayList<>();
        this.loadingDialog = new LottieLoadingDialog(this);
        initViews();
        initListeners();
        initDrawers();
        initAds();
        initNewest();
    }

    /* access modifiers changed from: protected */
    public void onStart() {
        super.onStart();
        getBoard();
    }

    private void showProgressDialog() {
        this.loadingDialog.show();
    }

    /* access modifiers changed from: private */
    public void hideProgressDialog() {
        this.loadingDialog.dismiss();
    }

    /* access modifiers changed from: private */
    public void getBoard() {
        showProgressDialog();
        this.database.getReference("Board/").limitToFirst(100).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            public void onSuccess(DataSnapshot dataSnapshot) {
                MainActivity.this.boardItems.clear();
                MainActivity.this.items2D.clear();
                MainActivity.this.items3D.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    BoardItem item = (BoardItem) snapshot.getValue(BoardItem.class);
                    item.setBoardID(snapshot.getKey());
                    MainActivity.this.boardItems.add(item);
                }
                Iterator<BoardItem> it = MainActivity.this.boardItems.iterator();
                while (it.hasNext()) {
                    BoardItem item2 = it.next();
                    Log.d("태그검사시작", "태그검사 시작");
                    List<String> tag = item2.getTags();
                    if (tag.contains("2d") || tag.contains("2d.*")) {
                        Log.d("들어있나?", "들어있네");
                        MainActivity.this.items2D.add(item2);
                    } else if (tag.contains("3d") || tag.contains("3d ")) {
                        MainActivity.this.items3D.add(item2);
                    }
                }
                Collections.sort(MainActivity.this.items2D, new BoardDateComparator());
                Collections.sort(MainActivity.this.items3D, new BoardDateComparator());
                MainActivity.this.boardRecyclerAdapter2D.notifyDataSetChanged();
                MainActivity.this.boardRecyclerAdapter3D.notifyDataSetChanged();
                MainActivity.this.hideProgressDialog();
            }
        }).addOnFailureListener(new OnFailureListener() {
            public void onFailure(Exception e) {
                MainActivity.this.hideProgressDialog();
                new AlertDialog.Builder(MainActivity.this).setTitle("게시글을 불러오지 못했습니다. 다시 시도해보세요.").setMessage("").setIcon(R.drawable.ic_baseline_back_hand_24).setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        MainActivity.this.getBoard();
                    }
                }).setNegativeButton("취소", (DialogInterface.OnClickListener) null).create().show();
            }
        });
    }

    private void initNewest() {
        Log.d("되니?", "되네");
        BoardRecyclerAdapter boardRecyclerAdapter = new BoardRecyclerAdapter(this, this.items2D);
        this.boardRecyclerAdapter2D = boardRecyclerAdapter;
        this.rv_newest2D.setAdapter(boardRecyclerAdapter);
        this.rv_newest2D.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        new PagerSnapHelper().attachToRecyclerView(this.rv_newest2D);
        BoardRecyclerAdapter boardRecyclerAdapter2 = new BoardRecyclerAdapter(this, this.items3D);
        this.boardRecyclerAdapter3D = boardRecyclerAdapter2;
        this.rv_newest3D.setAdapter(boardRecyclerAdapter2);
        this.rv_newest3D.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        new PagerSnapHelper().attachToRecyclerView(this.rv_newest3D);
        getBoard();
    }

    private void setTodayDesigner() {
        this.database.getReference("Users").get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            public void onSuccess(DataSnapshot dataSnapshot) {
                ArrayList<User> users = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Log.d("snapshot", snapshot.getValue().toString());
                    users.add((User) snapshot.getValue(User.class));
                }
            }
        });
    }

    /* access modifiers changed from: private */
    public void openCategoryDrawer() {
        this.drawerLayout.openDrawer((int) GravityCompat.START);
        this.wrap_layout.setClickable(false);
        this.wrap_layout.setEnabled(false);
    }

    /* access modifiers changed from: private */
    public void openMyInfoDrawer() {
        this.drawerLayout.openDrawer((int) GravityCompat.END);
        this.wrap_layout.setClickable(false);
        this.wrap_layout.setEnabled(false);
    }

    /* access modifiers changed from: private */
    public void insertBoardTest() {
        DatabaseReference upload = this.database.getReference("Board/").push();
        StorageReference storageRef = this.storage.getReference("Board/");
        new ArrayList();
        List<String> tags = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Bitmap bitmap = ((BitmapDrawable) getDrawable(R.drawable.example)).getBitmap();
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 80, bos);
            storageRef.child(upload.getKey()).child(Integer.toString(i)).putBytes(bos.toByteArray()).addOnCompleteListener((OnCompleteListener) new OnCompleteListener<UploadTask.TaskSnapshot>() {
                public void onComplete(Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()) {
                        Log.d("이미지 업로드", task.getResult().toString());
                    }
                }
            });
        }
        tags.add("2d");
        tags.add("2d 배경");
        tags.add("2d 디자인");
        upload.setValue(new BoardItem("ANpRQjoMP5dXQQZEO6iFJigeQBs2", "김태원", "3번째", "@@@@@@@@", tags, getDate()));
    }

    private String getDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = Calendar.getInstance().getTime();
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
        return sdf.format(date);
    }

    private void initViews() {
        this.tr_topRatedDesigners = (TableRow) findViewById(R.id.tr_topRatedDesigners);
        this.drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        EditText editText = (EditText) findViewById(R.id.et_search);
        this.et_search = editText;
        editText.clearFocus();
        this.wrap_layout = (LinearLayout) findViewById(R.id.wrap_layout);
        this.bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomnav_bottom_menu);
        this.vp_adsViewPager = (ViewPager2) findViewById(R.id.vp_adsViewPager);
        this.rv_newest2D = (RecyclerView) findViewById(R.id.rv_newest2D);
        this.rv_newest3D = (RecyclerView) findViewById(R.id.rv_newest3D);
    }

    private void initListeners() {
        this.et_search.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    MainActivity.this.et_search.clearFocus();
                    Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                    intent.putExtra("BoardItems", MainActivity.this.boardItems);
                    MainActivity.this.startActivity(intent);
                    MainActivity.this.overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                }
            }
        });
        findViewById(R.id.iv_myinfo).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                MainActivity.this.openMyInfoDrawer();
            }
        });
        findViewById(R.id.iv_category).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                MainActivity.this.openCategoryDrawer();
            }
        });
        this.bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            public boolean onNavigationItemSelected(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.bottom_category /*2131296356*/:
                        MainActivity.this.openCategoryDrawer();
                        return true;
                    case R.id.bottom_myinfo /*2131296358*/:
                        MainActivity.this.openMyInfoDrawer();
                        return true;
                    case R.id.bottom_search /*2131296359*/:
                        MainActivity.this.startActivity(new Intent(MainActivity.this, SearchActivity.class));
                        MainActivity.this.overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                        return true;
                    default:
                        return true;
                }
            }
        });
    }

    private void initDrawers() {
        this.li_2d_detail_category = (LinearLayout) findViewById(R.id.li_2d_detail_category);
        this.li_3d_detail_category = (LinearLayout) findViewById(R.id.li_3d_detail_category);
        this.li_plan_detail_category = (LinearLayout) findViewById(R.id.li_plan_detail_category);
        this.iv_2dDetail = (ImageView) findViewById(R.id.iv_2dDetail);
        this.iv_3dDetail = (ImageView) findViewById(R.id.iv_3dDetail);
        this.iv_planDetail = (ImageView) findViewById(R.id.iv_planDetail);
        this.li_2d_detail_category.setVisibility(View.GONE);
        this.li_3d_detail_category.setVisibility(View.GONE);
        this.li_plan_detail_category.setVisibility(View.GONE);
        this.drawerLayout.findViewById(R.id.iv_leftDrawerCancel).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                MainActivity.this.drawerLayout.closeDrawer((int) GravityCompat.START);
            }
        });
        this.drawerLayout.findViewById(R.id.li_2dCategory).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (MainActivity.this.li_2d_detail_category.getVisibility() == View.GONE) {
                    MainActivity.this.li_2d_detail_category.setVisibility(View.VISIBLE);
                    MainActivity.this.iv_2dDetail.setImageResource(R.drawable.ic_baseline_arrow_drop_down_24);
                    return;
                }
                MainActivity.this.li_2d_detail_category.setVisibility(View.GONE);
                MainActivity.this.iv_2dDetail.setImageResource(R.drawable.ic_baseline_arrow_right_24);
            }
        });
        this.drawerLayout.findViewById(R.id.li_3dCategory).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (MainActivity.this.li_3d_detail_category.getVisibility() == View.GONE) {
                    MainActivity.this.li_3d_detail_category.setVisibility(View.VISIBLE);
                    MainActivity.this.iv_3dDetail.setImageResource(R.drawable.ic_baseline_arrow_drop_down_24);
                    return;
                }
                MainActivity.this.li_3d_detail_category.setVisibility(View.GONE);
                MainActivity.this.iv_3dDetail.setImageResource(R.drawable.ic_baseline_arrow_right_24);
            }
        });
        this.drawerLayout.findViewById(R.id.li_plannerCategory).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (MainActivity.this.li_plan_detail_category.getVisibility() == View.GONE) {
                    MainActivity.this.li_plan_detail_category.setVisibility(View.VISIBLE);
                    MainActivity.this.iv_planDetail.setImageResource(R.drawable.ic_baseline_arrow_drop_down_24);
                    return;
                }
                MainActivity.this.li_plan_detail_category.setVisibility(View.GONE);
                MainActivity.this.iv_planDetail.setImageResource(R.drawable.ic_baseline_arrow_right_24);
            }
        });
        this.drawerLayout.findViewById(R.id.tv_2d_character).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                MainActivity.this.categoryIntent("2d 캐릭터");
            }
        });
        this.drawerLayout.findViewById(R.id.tv_2d_background).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                MainActivity.this.categoryIntent("2d 배경");
            }
        });
        this.drawerLayout.findViewById(R.id.tv_2d_animation).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                MainActivity.this.categoryIntent("2d 애니메이션");
            }
        });
        this.drawerLayout.findViewById(R.id.tv_3d_character).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                MainActivity.this.categoryIntent("3d 캐릭터");
            }
        });
        this.drawerLayout.findViewById(R.id.tv_3d_animation).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                MainActivity.this.categoryIntent("3d 애니메이션");
            }
        });
        this.drawerLayout.findViewById(R.id.tv_3d_background).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                MainActivity.this.categoryIntent("3d 배경");
            }
        });
        this.drawerLayout.findViewById(R.id.tv_3d_modeling).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                MainActivity.this.categoryIntent("3d 모델링");
            }
        });
        this.drawerLayout.findViewById(R.id.tv_plan_gameIdea).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                MainActivity.this.categoryIntent("게임아이디어");
            }
        });
        this.drawerLayout.findViewById(R.id.tv_plan_bgMusic).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                MainActivity.this.categoryIntent("배경음악");
            }
        });
        this.drawerLayout.findViewById(R.id.tv_plan_levelDesign).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                MainActivity.this.categoryIntent("레벨디자인");
            }
        });
        this.drawerLayout.findViewById(R.id.tv_plan_effectSound).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                MainActivity.this.categoryIntent("효과음");
            }
        });
        this.drawerLayout.findViewById(R.id.iv_rightDrawerCancel).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                MainActivity.this.drawerLayout.closeDrawer((int) GravityCompat.END);
            }
        });
        this.drawerLayout.findViewById(R.id.li_salesRegistrationBtn).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            }
        });
        this.drawerLayout.findViewById(R.id.li_saleItems).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            }
        });
        this.drawerLayout.findViewById(R.id.li_shoppingBasket).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            }
        });
        this.drawerLayout.findViewById(R.id.li_appInfo).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            }
        });
        this.drawerLayout.findViewById(R.id.li_serviceCenter).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            }
        });
        findViewById(R.id.btn_insertTest).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                MainActivity.this.insertBoardTest();
            }
        });
    }

    private void initAds() {
        AdsViewPagerAdapter adsViewPagerAdapter = new AdsViewPagerAdapter(this, this.adsItems);
        this.pagerAdapter = adsViewPagerAdapter;
        this.vp_adsViewPager.setAdapter(adsViewPagerAdapter);
        this.database.getReference().child("Ads").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            public void onComplete(Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Toast.makeText(MainActivity.this, "로딩 오류", Toast.LENGTH_SHORT).show();
                    return;
                }
                Log.d("FireBase", String.valueOf(task.getResult().getValue()));
                Map<String, Object> map = (Map) task.getResult().getValue();
                for (String keys : map.keySet()) {
                    Map<String, String> child = (Map) map.get(keys);
                    StorageReference storageRef = MainActivity.this.storage.getReference(child.get("image"));
                    Log.d("DownloadUrl", storageRef.getPath().toString());
                    MainActivity.this.adsItems.add(new AdsItem(storageRef.getPath(), child.get("ref")));
                }
                MainActivity.this.pagerAdapter.notifyDataSetChanged();
                final Handler handler = new Handler();
                final Runnable Update = new Runnable() {
                    public void run() {
                        if (MainActivity.this.currentAdsPage == MainActivity.this.adsItems.size()) {
                            MainActivity.this.currentAdsPage = 0;
                        }
                        ViewPager2 viewPager2 = MainActivity.this.vp_adsViewPager;
                        MainActivity mainActivity = MainActivity.this;
                        int i = mainActivity.currentAdsPage;
                        mainActivity.currentAdsPage = i + 1;
                        viewPager2.setCurrentItem(i, true);
                    }
                };
                MainActivity.this.adsTimer = new Timer();
                MainActivity.this.adsTimer.schedule(new TimerTask() {
                    public void run() {
                        handler.post(Update);
                    }
                }, 500, 3000);
            }
        });
    }

    /* access modifiers changed from: private */
    public void categoryIntent(String category) {
        Intent intent = new Intent(this, MainActivity2.class);
        intent.putExtra("category", category);
        intent.putExtra("BoardItems", this.boardItems);
        startActivity(intent);
    }
}
