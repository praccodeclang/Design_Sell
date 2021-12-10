package com.taewon.shoppingmall.activity;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.taewon.shoppingmall.R;
import com.taewon.shoppingmall.adapter.DownloadPicturePagerAdapter;
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;

import java.util.ArrayList;

public class FileDownloadActivity extends AppCompatActivity {
    String boardID;
    ViewPager2 rv_fileDownload_img;
    DotsIndicator fileDownload_dots_indicator;
    DownloadPicturePagerAdapter adapter;
    ArrayList<StorageReference> storageRefs;

    FirebaseStorage storage;
    FirebaseDatabase database;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_download);
        init();
        initViews();
    }
    void init(){
        boardID = getIntent().getStringExtra("BoardID");
        storageRefs = new ArrayList<>();
        storage = FirebaseStorage.getInstance();
        database = FirebaseDatabase.getInstance();
    }
    void initViews(){
        fileDownload_dots_indicator = findViewById(R.id.fileDownload_dots_indicator);
        rv_fileDownload_img = findViewById(R.id.rv_fileDownload_img);
        
        storage.getReference("Board").child(boardID)
                .listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
                for(StorageReference ref : listResult.getItems()){
                    storageRefs.add(ref);
                }
                adapter.notifyDataSetChanged();
            }
        });
        adapter = new DownloadPicturePagerAdapter(FileDownloadActivity.this, storageRefs);
        rv_fileDownload_img.setAdapter(adapter);
        fileDownload_dots_indicator.setViewPager2(rv_fileDownload_img);
    }
    void initListeners(){

    }
}
