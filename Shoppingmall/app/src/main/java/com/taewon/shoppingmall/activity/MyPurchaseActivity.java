package com.taewon.shoppingmall.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager2.widget.ViewPager2;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.taewon.shoppingmall.R;
import com.taewon.shoppingmall.adapter.BoardRecyclerAdapter;
import com.taewon.shoppingmall.item.BoardItem;
import com.taewon.shoppingmall.util.BoardDateComparator;
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

public class MyPurchaseActivity extends AppCompatActivity {
    FirebaseAuth mAuth;
    FirebaseDatabase database;
    ArrayList<BoardItem> purchaseItems;

    int totalPrice = 0;
    LinearLayout li_purchase_empty;
    SwipeRefreshLayout swipe_purchase_wrap;
    ViewPager2 rv_purchase;
    TextView tv_purchase_totalPrice;
    BoardRecyclerAdapter adapter;
    DotsIndicator purchase_indicator;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase);
        init();
        initViews();
        initListeners();
    }

    @Override
    protected void onStart() {
        super.onStart();
        getPurchase();
    }

    void getPurchase(){
        totalPrice = 0;
        purchaseItems.clear();
        ArrayList<String> purchaseID = new ArrayList<>();
        //구매 목록 가져오기
        database.getReference("Sales").get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    if(snapshot.hasChild(mAuth.getCurrentUser().getUid())){
                        Log.d("스냅샷 키", snapshot.getKey());
                        purchaseID.add(snapshot.getKey());
                    }
                }

                database.getReference("Board")
                        .get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                    @Override
                    public void onSuccess(DataSnapshot dataSnapshot) {
                        for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                            if(purchaseID.contains(snapshot.getKey())){
                                BoardItem item = snapshot.getValue(BoardItem.class);
                                totalPrice += item.getPrice();
                                purchaseItems.add(item);
                            }
                        }
                        if(purchaseItems.size() > 0){
                            li_purchase_empty.setVisibility(View.GONE);
                        }
                        else{
                            li_purchase_empty.setVisibility(View.VISIBLE);
                        }
                        Collections.sort(purchaseItems, new BoardDateComparator());
                        adapter.notifyDataSetChanged();
                        tv_purchase_totalPrice.setText(totalPrice + "");
                    }
                });
            }
        });

    }

    void init(){
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        purchaseItems = new ArrayList<>();
    }

    void initViews(){
        li_purchase_empty = findViewById(R.id.li_purchase_empty);
        swipe_purchase_wrap = findViewById(R.id.swipe_purchase_wrap);
        tv_purchase_totalPrice = findViewById(R.id.tv_purchase_totalPrice);
        purchase_indicator = findViewById(R.id.purchase_indicator);

        rv_purchase = findViewById(R.id.rv_purchase);
        adapter = new BoardRecyclerAdapter(MyPurchaseActivity.this, purchaseItems);
        rv_purchase.setAdapter(adapter);
        purchase_indicator.setViewPager2(rv_purchase);
    }

    void initListeners(){
        swipe_purchase_wrap.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getPurchase();
                swipe_purchase_wrap.setRefreshing(false);
            }
        });
    }
}
