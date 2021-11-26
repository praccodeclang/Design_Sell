package com.taewon.shoppingmall.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.taewon.shoppingmall.R;
import com.taewon.shoppingmall.adapter.SearchWordAdapter;
import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {
    private ArrayList<String> arrayList;
    /* access modifiers changed from: private */
    public EditText et_searchText;
    private List<String> list;
    private ListView lv_search;
    /* access modifiers changed from: private */
    public SearchWordAdapter searchWordAdapter;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_search);
        init();
        initViews();
        initListeners();
    }

    private void init() {
        this.list = new ArrayList();
        this.arrayList = new ArrayList<>();
        this.lv_search = (ListView) findViewById(R.id.lv_search);
        settingList();
        this.arrayList.addAll(this.list);
        SearchWordAdapter searchWordAdapter2 = new SearchWordAdapter(this, this.list);
        this.searchWordAdapter = searchWordAdapter2;
        this.lv_search.setAdapter(searchWordAdapter2);
    }

    private void initViews() {
        this.et_searchText = (EditText) findViewById(R.id.et_searchText);
    }

    private void initListeners() {
        this.et_searchText.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            public void afterTextChanged(Editable s) {
                SearchActivity.this.search(SearchActivity.this.et_searchText.getText().toString());
            }
        });
        this.et_searchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                Intent intent = new Intent(SearchActivity.this, MainActivity2.class);
                intent.putExtra("category", SearchActivity.this.et_searchText.getText().toString());
                intent.putExtra("BoardItems", SearchActivity.this.getIntent().getSerializableExtra("BoardItems"));
                SearchActivity.this.startActivity(intent);
                SearchActivity.this.finish();
                return false;
            }
        });
        this.lv_search.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent = new Intent(SearchActivity.this, MainActivity2.class);
                intent.putExtra("category", SearchActivity.this.searchWordAdapter.getItem(position));
                intent.putExtra("BoardItems", SearchActivity.this.getIntent().getSerializableExtra("BoardItems"));
                SearchActivity.this.startActivity(intent);
                SearchActivity.this.finish();
            }
        });
    }

    private void settingList() {
        this.list.add("2d");
        this.list.add("2d 캐릭터");
        this.list.add("2d 배경");
        this.list.add("2d 애니메이션");
        this.list.add("3d");
        this.list.add("3d 캐릭터");
        this.list.add("3d 배경");
        this.list.add("3d 애니메이션");
        this.list.add("3d 모델링");
        this.list.add("게임 기획");
        this.list.add("레벨 디자인");
    }

    /* access modifiers changed from: private */
    public void search(String text) {
        this.list.clear();
        if (text.isEmpty()) {
            this.list.addAll(this.arrayList);
        } else {
            for (int i = 0; i < this.arrayList.size(); i++) {
                if (this.arrayList.get(i).contains(text)) {
                    this.list.add(this.arrayList.get(i));
                }
            }
        }
        this.searchWordAdapter.notifyDataSetChanged();
    }

    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.slide_enter, R.anim.slide_exit);
    }
}
