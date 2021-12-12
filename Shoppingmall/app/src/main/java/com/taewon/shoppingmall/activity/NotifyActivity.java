package com.taewon.shoppingmall.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.taewon.shoppingmall.R;
import com.taewon.shoppingmall.adapter.NotifyRecyclerAdapter;
import com.taewon.shoppingmall.item.NotifyItem;
import com.taewon.shoppingmall.util.NotifyDateComparator;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;

public class NotifyActivity extends AppCompatActivity {
    FirebaseAuth mAuth;
    FirebaseDatabase database;
    ArrayList<NotifyItem> notifyItems;
    RecyclerView rv_notify;
    NotifyRecyclerAdapter adapter;
    LinearLayout li_notify_empty;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notify);
        init();
        initViews();
        initListeners();
    }

    @Override
    protected void onStart() {
        super.onStart();
        getNotify();
    }

    void init(){
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        notifyItems = new ArrayList<>();
    }

    void initViews(){
        rv_notify = findViewById(R.id.rv_notify);
        adapter = new NotifyRecyclerAdapter(NotifyActivity.this, notifyItems);
        rv_notify.setAdapter(adapter);
        rv_notify.setLayoutManager(new LinearLayoutManager(NotifyActivity.this));
        li_notify_empty = findViewById(R.id.li_notify_empty);
    }

    void initListeners(){

    }

    void getNotify(){
        notifyItems.clear();
        DatabaseReference ref = database.getReference("Notify").child(mAuth.getCurrentUser().getUid());
        ref.get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                    @Override
                    public void onSuccess(DataSnapshot dataSnapshot) {
                        for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                            NotifyItem item = snapshot.getValue(NotifyItem.class);
                            notifyItems.add(item);
                            snapshot.getRef().child("isRead").setValue(true);
                        }
                        if(notifyItems.size() > 0){
                            li_notify_empty.setVisibility(View.GONE);
                        }
                        else{
                            li_notify_empty.setVisibility(View.VISIBLE);
                        }
                        Collections.sort(notifyItems, new NotifyDateComparator());
                        adapter.notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

    }
}
