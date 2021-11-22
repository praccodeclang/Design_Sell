package com.taewon.shoppingmall.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.taewon.shoppingmall.R;
import com.taewon.shoppingmall.adapter.SearchAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SearchActivity extends AppCompatActivity {
    private EditText et_searchText;
    private List<String> list;
    private ArrayList<String> arrayList;
    private ListView lv_search;
    private SearchAdapter searchAdapter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        init();
        initViews();
        initListeners();
    }

    private void init(){
        list = new ArrayList<>();
        arrayList = new ArrayList<>();
        lv_search = findViewById(R.id.lv_search);
        settingList();
        arrayList.addAll(list);
        searchAdapter = new SearchAdapter(SearchActivity.this, list);
        lv_search.setAdapter(searchAdapter);

    }

    private void initViews(){
        et_searchText = findViewById(R.id.et_searchText);
    }
    private void initListeners(){
        et_searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = et_searchText.getText().toString();
                search(text);
            }
        });

        lv_search.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                et_searchText.setText(searchAdapter.getItem(position));
            }
        });

    }


    private void settingList(){
        list.add("2D");
        list.add("2D 캐릭터");
        list.add("2D 배경");
        list.add("2D 애니메이션");
        list.add("3D");
        list.add("3D 캐릭터");
        list.add("3D 배경");
        list.add("3D 애니메이션");
        list.add("3D 모델링");
        list.add("게임 기획");
        list.add("레벨 디자인");
    }

    private void search(String text){
        list.clear();
        if(text.isEmpty()){
            list.addAll(arrayList);
        }
        else{
            for(int i=0; i< arrayList.size(); i++){
                if(arrayList.get(i).contains(text)){
                    list.add(arrayList.get(i));
                }
            }
        }
        searchAdapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.slide_enter, R.anim.slide_exit);
    }
}
