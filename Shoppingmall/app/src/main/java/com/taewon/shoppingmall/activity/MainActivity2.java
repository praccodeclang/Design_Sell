package com.taewon.shoppingmall.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.taewon.shoppingmall.R;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity2 extends AppCompatActivity {
    Intent categoryIntent;
    TextView tv_category;
    RecyclerView rv_boardRecycler;
    FirebaseDatabase database;
    FirebaseStorage storage;
    FirebaseAuth mAuth;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        init();
        initViews();
    }

    private void init(){
        categoryIntent = getIntent();
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
    }

    private void initViews(){
        setContentView(R.layout.activity_main2);
        tv_category = findViewById(R.id.tv_category);
        tv_category.setText(categoryIntent.getStringExtra("category").toUpperCase());
        rv_boardRecycler = findViewById(R.id.rv_boardRecycler);
    }
}
