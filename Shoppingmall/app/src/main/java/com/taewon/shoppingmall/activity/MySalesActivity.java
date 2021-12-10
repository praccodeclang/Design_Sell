package com.taewon.shoppingmall.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.taewon.shoppingmall.R;
import com.taewon.shoppingmall.adapter.BoardRecyclerAdapter;
import com.taewon.shoppingmall.item.BoardItem;
import com.taewon.shoppingmall.util.BoardDateComparator;
import com.taewon.shoppingmall.util.BoardStarCountComparator;

import java.util.ArrayList;
import java.util.Collections;

public class MySalesActivity extends AppCompatActivity {
    FirebaseAuth mAuth;
    FirebaseStorage storage;
    FirebaseDatabase database;
    ArrayList<BoardItem> items;

    LinearLayout li_sales_empty;
    RecyclerView rv_my_sales;
    BoardRecyclerAdapter adapter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_sales);
        init();
        initViews();
        initListeners();
    }

    @Override
    protected void onStart() {
        super.onStart();
        getBoard();
    }

    private void init(){
        mAuth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        database = FirebaseDatabase.getInstance();
        items = new ArrayList<>();
    }

    private void initViews(){
        li_sales_empty = findViewById(R.id.li_sales_empty);

        rv_my_sales = findViewById(R.id.rv_my_sales);
        adapter = new BoardRecyclerAdapter(MySalesActivity.this, items);
        rv_my_sales.setLayoutManager(new LinearLayoutManager(MySalesActivity.this, RecyclerView.HORIZONTAL, false));
        rv_my_sales.setAdapter(adapter);
        SnapHelper helper = new LinearSnapHelper();
        helper.attachToRecyclerView(rv_my_sales);
    }

    private void initListeners(){

    }

    private void getBoard(){
        database.getReference("Board").get()
                .addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                    @Override
                    public void onSuccess(DataSnapshot dataSnapshot) {
                        for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                            BoardItem item = snapshot.getValue(BoardItem.class);
                            if(item.getUid().equals(mAuth.getCurrentUser().getUid())){
                                items.add(item);
                            }
                        }
                        if(items.size() > 0){
                            li_sales_empty.setVisibility(View.GONE);
                        }
                        else{
                            li_sales_empty.setVisibility(View.VISIBLE);
                        }
                        Collections.sort(items, new BoardStarCountComparator());
                        adapter.notifyDataSetChanged();
                    }
                });
    }

}
