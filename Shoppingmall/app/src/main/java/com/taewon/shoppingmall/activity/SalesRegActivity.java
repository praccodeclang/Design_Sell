package com.taewon.shoppingmall.activity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ToggleButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.taewon.shoppingmall.R;

public class SalesRegActivity extends AppCompatActivity {
    final private int GET_PHOTO_IN_GALLERY = 2222;
    FirebaseAuth mAuth;
    FirebaseDatabase database;
    FirebaseStorage storage;

    /* Views */
    EditText et_sales_title;
    LinearLayout li_sales_addUserPhotoBtn;
    RecyclerView rv_salesImg;
    EditText et_sales_body;

    ToggleButton rd_sales_2d_tag;
    ToggleButton rd_sales_2d_character_tag;
    ToggleButton rd_sales_2d_background_tag;
    ToggleButton rd_sales_2d_anim_tag;

    ToggleButton rd_sales_3d_tag;
    ToggleButton rd_sales_3d_character_tag;
    ToggleButton rd_sales_3d_background_tag;
    ToggleButton rd_sales_3d_anim_tag;
    ToggleButton rd_sales_3d_modeling_tag;

    ToggleButton rd_sales_plan_gameplan_tag;
    ToggleButton rd_sales_plan_level_design_tag;
    ToggleButton rd_sales_plan_bgm_tag;
    ToggleButton rd_sales_plan_effect_sound_tag;

    Button btn_send_registration;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_registration);
        init();
        initViews();
        initListeners();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void init(){
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
    }

    private void initViews(){
        et_sales_title = findViewById(R.id.et_sales_title);
        li_sales_addUserPhotoBtn = findViewById(R.id.li_sales_addUserPhotoBtn);
        rv_salesImg = findViewById(R.id.rv_salesImg);
        et_sales_body = findViewById(R.id.et_sales_body);

        rd_sales_2d_tag = findViewById(R.id.rd_sales_2d_tag);
        rd_sales_2d_character_tag = findViewById(R.id.rd_sales_2d_character_tag);
        rd_sales_2d_background_tag = findViewById(R.id.rd_sales_2d_background_tag);
        rd_sales_2d_anim_tag = findViewById(R.id.rd_sales_2d_anim_tag);

        rd_sales_3d_tag = findViewById(R.id.rd_sales_3d_tag);
        rd_sales_3d_character_tag = findViewById(R.id.rd_sales_3d_character_tag);
        rd_sales_3d_background_tag = findViewById(R.id.rd_sales_3d_background_tag);
        rd_sales_3d_anim_tag = findViewById(R.id.rd_sales_3d_anim_tag);
        rd_sales_3d_modeling_tag = findViewById(R.id.rd_sales_3d_modeling_tag);

        rd_sales_plan_gameplan_tag = findViewById(R.id.rd_sales_plan_gameplan_tag);
        rd_sales_plan_level_design_tag = findViewById(R.id.rd_sales_plan_level_design_tag);
        rd_sales_plan_bgm_tag = findViewById(R.id.rd_sales_plan_bgm_tag);
        rd_sales_plan_effect_sound_tag = findViewById(R.id.rd_sales_plan_effect_sound_tag);

        btn_send_registration = findViewById(R.id.btn_send_registration);
    }

    private void initListeners(){
        li_sales_addUserPhotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);  // 다중 이미지를 가져올 수 있도록 세팅
                intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, GET_PHOTO_IN_GALLERY);
            }
        });
    }



    private void checkSalesRegCondition(){

    }
}
