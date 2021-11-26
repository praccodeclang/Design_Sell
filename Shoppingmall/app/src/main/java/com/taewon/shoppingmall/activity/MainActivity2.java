package com.taewon.shoppingmall.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

import com.taewon.shoppingmall.R;
import com.taewon.shoppingmall.adapter.BoardRecyclerAdapter;
import com.taewon.shoppingmall.item.BoardItem;
import com.taewon.shoppingmall.util.BoardDateComparator;
import com.taewon.shoppingmall.util.BoardStarCountComparator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

public class MainActivity2 extends AppCompatActivity {
    BoardRecyclerAdapter boardRecyclerAdapter;
    Intent categoryIntent;
    String categoryString;
    FirebaseDatabase database;
    ArrayList<BoardItem> itemList;
    FirebaseAuth mAuth;
    RecyclerView rv_boardRecycler;
    ArrayList<BoardItem> searchItemList;
    Spinner sp_orderSpinner;
    FirebaseStorage storage;
    TextView tv_category;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_main2);
        init();
        initViews();
    }

    private void init() {
        Intent intent = getIntent();
        this.categoryIntent = intent;
        this.categoryString = intent.getStringExtra("category");
        this.mAuth = FirebaseAuth.getInstance();
        this.database = FirebaseDatabase.getInstance();
        this.storage = FirebaseStorage.getInstance();
        this.itemList = (ArrayList) this.categoryIntent.getSerializableExtra("BoardItems");
        this.searchItemList = new ArrayList<>();
        Iterator<BoardItem> it = this.itemList.iterator();
        while (it.hasNext()) {
            BoardItem item = it.next();
            if (item.getTags().contains(this.categoryString)) {
                this.searchItemList.add(item);
            }
        }
    }

    private void initViews() {
        setContentView((int) R.layout.activity_main2);
        TextView textView = (TextView) findViewById(R.id.tv_category);
        this.tv_category = textView;
        textView.setText(this.categoryString.toUpperCase());
        this.rv_boardRecycler = (RecyclerView) findViewById(R.id.rv_boardRecycler);
        this.sp_orderSpinner = (Spinner) findViewById(R.id.sp_orderSpinner);
        setItems();
    }

    private void setItems() {
        if (this.searchItemList.size() < 1) {
            this.rv_boardRecycler.setVisibility(View.GONE);
            this.sp_orderSpinner.setVisibility(View.GONE);
            return;
        }
        Collections.sort(this.searchItemList, new BoardDateComparator());
        this.boardRecyclerAdapter = new BoardRecyclerAdapter(this, this.searchItemList);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(RecyclerView.VERTICAL);
        new PagerSnapHelper().attachToRecyclerView(this.rv_boardRecycler);
        this.rv_boardRecycler.setLayoutManager(manager);
        this.rv_boardRecycler.setAdapter(this.boardRecyclerAdapter);
        ArrayAdapter adapter = ArrayAdapter.createFromResource(this, R.array.spinner_items, R.layout.support_simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        this.sp_orderSpinner.setAdapter(adapter);
        this.sp_orderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                switch (position) {
                    case 0:
                        Collections.sort(MainActivity2.this.searchItemList, new BoardDateComparator());
                        break;
                    case 1:
                        Collections.sort(MainActivity2.this.searchItemList, new BoardDateComparator(true));
                        break;
                    case 2:
                        Collections.sort(MainActivity2.this.searchItemList, new BoardStarCountComparator());
                        break;
                }
                MainActivity2.this.boardRecyclerAdapter.notifyDataSetChanged();
            }

            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }
}