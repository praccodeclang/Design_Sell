package com.taewon.shoppingmall.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.taewon.shoppingmall.R;
import com.taewon.shoppingmall.util.BoardDateComparator;
import com.taewon.shoppingmall.util.BoardStarCountComparator;

import java.util.ArrayList;
import java.util.Collections;

public class RegisterActivity extends AppCompatActivity {
    private Spinner sp_emailSpinner;
    TextView tv_passwordGuide;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        tv_passwordGuide = findViewById(R.id.tv_passwordGuide);

        sp_emailSpinner = (Spinner) findViewById(R.id.sp_emailSpinner);
        ArrayAdapter adapter = ArrayAdapter.createFromResource(this, R.array.register_spinner_items, R.layout.support_simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        sp_emailSpinner.setAdapter(adapter);
        sp_emailSpinner.setSelection(0);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.slide_enter, R.anim.slide_exit);
    }
}