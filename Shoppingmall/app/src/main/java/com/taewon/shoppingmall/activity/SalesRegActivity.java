package com.taewon.shoppingmall.activity;

import android.content.ClipData;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.taewon.shoppingmall.R;
import com.taewon.shoppingmall.adapter.SalesPictureRecyclerAdapter;
import com.taewon.shoppingmall.dialog.LottieLoadingDialog;
import com.taewon.shoppingmall.item.BoardItem;
import com.taewon.shoppingmall.item.User;
import com.taewon.shoppingmall.util.GalleryUtil;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class SalesRegActivity extends AppCompatActivity {
    final private int GET_PHOTO_IN_GALLERY = 2222;
    FirebaseAuth mAuth;
    FirebaseDatabase database;
    FirebaseStorage storage;
    LottieLoadingDialog loadingDialog;
    ArrayList<String> tags;

    GalleryUtil galleryUtil;
    ArrayList<Bitmap> photoItems;

    /* Views */
    EditText et_sales_title;
    LinearLayout li_sales_addUserPhotoBtn;
    RecyclerView rv_salesImg;
    SalesPictureRecyclerAdapter adapter;

    EditText et_sales_body;
    EditText et_sales_price;

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
        switch (requestCode){
            case GET_PHOTO_IN_GALLERY :
                boolean isNull = galleryUtil.photoDataIsNull(data);
                if(isNull){
                    Toast.makeText(SalesRegActivity.this, "이미지를 선택하지 않았습니다.", Toast.LENGTH_LONG).show();
                    return; }
                if(photoItems.size()>10)
                {
                    //선택한 사진이 10장이 넘는다면 함수를 종료.
                    Toast.makeText(SalesRegActivity.this, "사진은 4장까지 선택할 수 있습니다.", Toast.LENGTH_LONG).show();
                    return;
                }
                if(data.getClipData() == null){
                    //다중 선택이 아니면 실행.
                    Uri instance = data.getData();
                    String imagePath = galleryUtil.getRealPathFromURI(instance);
                    Bitmap bitmap;
                    try {
                        bitmap = galleryUtil.rotateBitmapWithExif(imagePath);
                    }catch (Exception e){
                        e.printStackTrace();
                        bitmap = galleryUtil.readImageWithSampling(imagePath, rv_salesImg.getWidth(), rv_salesImg.getHeight());
                    }
                    photoItems.add(bitmap);
                }
                else{
                    //다중 선택이면 실행.
                    li_sales_addUserPhotoBtn.setVisibility(View.GONE);
                    ClipData clipData = data.getClipData();

                    for(int i=0; i<clipData.getItemCount(); i++){
                        Uri instance = clipData.getItemAt(i).getUri();
                        String imagePath = galleryUtil.getRealPathFromURI(instance);
                        Bitmap bitmap;
                        try {
                            bitmap = galleryUtil.rotateBitmapWithExif(imagePath);
                        }catch (Exception e){
                            e.printStackTrace();
                            bitmap = galleryUtil.readImageWithSampling(imagePath, rv_salesImg.getWidth(), rv_salesImg.getHeight());
                        }
                        photoItems.add(bitmap);
                    }
                }
                adapter.notifyDataSetChanged();
                break;
        }
    }

    private void init(){
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
        tags = new ArrayList<>();
        photoItems = new ArrayList<>();
        galleryUtil = new GalleryUtil(SalesRegActivity.this);
        loadingDialog = new LottieLoadingDialog(SalesRegActivity.this);
    }

    private void initViews(){
        et_sales_title = findViewById(R.id.et_sales_title);
        li_sales_addUserPhotoBtn = findViewById(R.id.li_sales_addUserPhotoBtn);
        rv_salesImg = findViewById(R.id.rv_salesImg);
        adapter = new SalesPictureRecyclerAdapter(SalesRegActivity.this, photoItems);
        rv_salesImg.setAdapter(adapter);
        rv_salesImg.setLayoutManager(new LinearLayoutManager(SalesRegActivity.this, RecyclerView.HORIZONTAL, false));
        et_sales_body = findViewById(R.id.et_sales_body);
        et_sales_price = findViewById(R.id.et_sales_price);

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

        //게시글 작성
        btn_send_registration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkSalesRegCondition();
            }
        });
        
        //토글버튼
        rd_sales_2d_tag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleBtnClicked(rd_sales_2d_tag);
            }
        });
        rd_sales_2d_character_tag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleBtnClicked(rd_sales_2d_character_tag);
            }
        });
        rd_sales_2d_background_tag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleBtnClicked(rd_sales_2d_background_tag);
            }
        });
        rd_sales_2d_anim_tag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleBtnClicked(rd_sales_2d_anim_tag);
            }
        });
        rd_sales_3d_tag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleBtnClicked(rd_sales_3d_tag);
            }
        });
        rd_sales_3d_character_tag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleBtnClicked(rd_sales_3d_character_tag);
            }
        });
        rd_sales_3d_anim_tag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleBtnClicked(rd_sales_3d_anim_tag);
            }
        });
        rd_sales_3d_modeling_tag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleBtnClicked(rd_sales_3d_modeling_tag);
            }
        });
        rd_sales_plan_gameplan_tag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleBtnClicked(rd_sales_plan_gameplan_tag);
            }
        });
        rd_sales_plan_level_design_tag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleBtnClicked(rd_sales_plan_level_design_tag);
            }
        });
        rd_sales_plan_bgm_tag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleBtnClicked(rd_sales_plan_bgm_tag);
            }
        });
        rd_sales_plan_effect_sound_tag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleBtnClicked(rd_sales_plan_effect_sound_tag);
            }
        });

    }


    private void toggleBtnClicked(ToggleButton btn){
        if(btn.isChecked()){
            tags.add(btn.getText().toString());
            Log.d("insert?", tags.toString());
        }
        else{
            tags.remove(btn.getText().toString());
            Log.d("delete?", tags.toString());
        }
    }
    private void checkSalesRegCondition(){
        loadingDialog.show();
        String title = et_sales_title.getText().toString().trim();
        String body = et_sales_body.getText().toString();
        String price = et_sales_price.getText().toString().trim();
        if( title == "" || title.isEmpty()){
            Toast.makeText(SalesRegActivity.this, "제목을 입력하세요.", Toast.LENGTH_SHORT).show();
            loadingDialog.dismiss();
            return;
        }
        if(tags.size() < 1){
            Toast.makeText(SalesRegActivity.this, "태그를 선택해주세요.", Toast.LENGTH_SHORT).show();
            loadingDialog.dismiss();
            return;
        }
        if(photoItems.size() < 1){
            Toast.makeText(SalesRegActivity.this, "사진을 1장은 선택해야합니다.", Toast.LENGTH_SHORT).show();
            loadingDialog.dismiss();
            return;
        }
        if(price.isEmpty() || price == ""){
            Toast.makeText(SalesRegActivity.this, "가격을 입력해주세요.", Toast.LENGTH_SHORT).show();
            loadingDialog.dismiss();
            return;
        }
        try {
            int temp = Integer.parseInt(price);
            if(temp < 0){
                Toast.makeText(SalesRegActivity.this, "가격은 음수일 수 없습니다.", Toast.LENGTH_SHORT).show();
                loadingDialog.dismiss();
                return;
            }
        }
        catch (Exception e){
            Toast.makeText(SalesRegActivity.this, "가격은 숫자입니다.", Toast.LENGTH_SHORT).show();
            loadingDialog.dismiss();
            return;
        }
        insertBoard(title, body, price);
    }

    private void insertBoard(String title, String body, String price){
        DatabaseReference upload = database.getReference("Board/").push();
        StorageReference storageRef = storage.getReference("Board/");
        int i = 0;
        for (Bitmap bitmap : photoItems) {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 80, bos);
            storageRef.child(upload.getKey()).child(Integer.toString(i))
                    .putBytes(bos.toByteArray())
                    .addOnCompleteListener((OnCompleteListener) new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        public void onComplete(Task<UploadTask.TaskSnapshot> task) {
                            if (task.isSuccessful()) {
                                Log.d("이미지 업로드", task.getResult().toString());
                            }
                        }
                    });
            i++;
        }

        database.getReference("Users").child(mAuth.getCurrentUser().getUid())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                    @Override
                    public void onSuccess(DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);

                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        Date date = Calendar.getInstance().getTime();
                        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));

                        BoardItem instance = new BoardItem(user.getUid(), user.getUsername(), title, body, tags, Integer.parseInt(price), false, sdf.format(date));
                        instance.setBoardID(upload.getKey());
                        upload.setValue(instance)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(SalesRegActivity.this, "글을 작성했습니다.", Toast.LENGTH_SHORT).show();
                                        loadingDialog.dismiss();
                                        onBackPressed();
                                    }
                                });
                    }
                });
    }
}
