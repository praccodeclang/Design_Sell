package com.taewon.shoppingmall.activity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.taewon.shoppingmall.R;
import com.taewon.shoppingmall.adapter.BoardRecyclerAdapter;
import com.taewon.shoppingmall.item.BoardItem;
import com.taewon.shoppingmall.util.BoardDateComparator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

public class MyPurchaseActivity extends AppCompatActivity {
    FirebaseAuth mAuth;
    FirebaseDatabase database;
    ArrayList<BoardItem> purchaseItems;

    int totalPrice = 0;
    SwipeRefreshLayout swipe_purchase_wrap;
    RecyclerView rv_purchase;
    TextView tv_purchase_totalPrice;
    BoardRecyclerAdapter adapter;
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
        swipe_purchase_wrap = findViewById(R.id.swipe_purchase_wrap);
        tv_purchase_totalPrice = findViewById(R.id.tv_purchase_totalPrice);

        rv_purchase = findViewById(R.id.rv_purchase);
        adapter = new BoardRecyclerAdapter(MyPurchaseActivity.this, purchaseItems);
        rv_purchase.setAdapter(adapter);
        rv_purchase.setLayoutManager(new LinearLayoutManager(MyPurchaseActivity.this));

        SnapHelper helper = new LinearSnapHelper();
        helper.attachToRecyclerView(rv_purchase);
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
