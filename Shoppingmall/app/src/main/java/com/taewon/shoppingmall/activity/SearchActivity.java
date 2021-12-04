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
    public EditText et_searchText;
    private List<String> list;
    private ListView lv_search;
    public SearchWordAdapter searchWordAdapter;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        init();
        initViews();
        initListeners();
    }

    private void init() {
        list = new ArrayList();
        arrayList = new ArrayList<>();
        lv_search = (ListView) findViewById(R.id.lv_search);
        settingList();
        arrayList.addAll(list);
        SearchWordAdapter searchWordAdapter2 = new SearchWordAdapter(this, list);
        searchWordAdapter = searchWordAdapter2;
        lv_search.setAdapter(searchWordAdapter2);
    }

    private void initViews() {
        et_searchText = (EditText) findViewById(R.id.et_searchText);
    }

    private void initListeners() {
        et_searchText.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            public void afterTextChanged(Editable s) {
                search(et_searchText.getText().toString());
            }
        });
        et_searchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                Intent intent = new Intent(SearchActivity.this, MainActivity2.class);
                intent.putExtra("category", et_searchText.getText().toString());
                startActivity(intent);
                onBackPressed();
                finish();
                return false;
            }
        });
        lv_search.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent = new Intent(SearchActivity.this, MainActivity2.class);
                intent.putExtra("category", searchWordAdapter.getItem(position));
                startActivity(intent);
                onBackPressed();
                finish();
            }
        });
    }

    private void settingList() {
        list.add("2d");
        list.add("2d 캐릭터");
        list.add("2d 배경");
        list.add("2d 애니메이션");
        list.add("3d");
        list.add("3d 캐릭터");
        list.add("3d 배경");
        list.add("3d 애니메이션");
        list.add("3d 모델링");
        list.add("게임 기획");
        list.add("레벨 디자인");
        list.add("배경음악");
        list.add("효과음");
    }
    
    //
    public void search(String text) {
        list.clear();
        if (text.isEmpty()) {
            list.addAll(arrayList);
        } else {
            for (int i = 0; i < arrayList.size(); i++) {
                if (arrayList.get(i).contains(text)) {
                    list.add(arrayList.get(i));
                }
            }
        }
        searchWordAdapter.notifyDataSetChanged();
    }

    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.slide_enter, R.anim.slide_exit);
    }
}
