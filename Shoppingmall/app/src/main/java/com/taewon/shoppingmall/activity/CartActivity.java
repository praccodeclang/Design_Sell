package com.taewon.shoppingmall.activity;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.taewon.shoppingmall.R;
import com.taewon.shoppingmall.adapter.BoardRecyclerAdapter;
import com.taewon.shoppingmall.item.BoardItem;

import java.util.ArrayList;

public class CartActivity extends AppCompatActivity {
    FirebaseAuth mAuth;
    FirebaseDatabase database;
    ArrayList<BoardItem> cartItems;

    RecyclerView rv_cart;
    BoardRecyclerAdapter adapter;
    LottieAnimationView lottie_cart;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        init();
        initViews();
        initListeners();
    }

    @Override
    protected void onStart() {
        super.onStart();
        getCart();
    }

    void getCart(){
        cartItems.clear();
        database.getReference("Cart").child(mAuth.getCurrentUser().getUid())
                .get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    database.getReference("Board").child(snapshot.getKey()).get()
                    .addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                        @Override
                        public void onSuccess(DataSnapshot dataSnapshot) {
                            cartItems.add(dataSnapshot.getValue(BoardItem.class));
                            adapter.notifyDataSetChanged();
                        }
                    });
                }

            }
        });
    }

    void init(){
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        cartItems = new ArrayList<>();
    }

    void initViews(){
        rv_cart = findViewById(R.id.rv_cart);
        adapter = new BoardRecyclerAdapter(CartActivity.this, cartItems);
        rv_cart.setAdapter(adapter);
        rv_cart.setLayoutManager(new LinearLayoutManager(CartActivity.this));
        SnapHelper helper = new LinearSnapHelper();
        helper.attachToRecyclerView(rv_cart);

        lottie_cart = findViewById(R.id.lottie_cartView_cart);
    }

    void initListeners(){

    }
}
