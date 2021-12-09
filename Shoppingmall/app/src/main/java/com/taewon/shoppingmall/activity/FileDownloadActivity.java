package com.taewon.shoppingmall.activity;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.taewon.shoppingmall.R;
import com.taewon.shoppingmall.adapter.DownloadPictureRecyclerAdapter;

import java.util.ArrayList;

public class FileDownloadActivity extends AppCompatActivity {
    String boardID;
    RecyclerView rv_fileDownload_img;
    DownloadPictureRecyclerAdapter adapter;
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
        rv_fileDownload_img = findViewById(R.id.rv_fileDownload_img);
        adapter = new DownloadPictureRecyclerAdapter(FileDownloadActivity.this, storageRefs);
        rv_fileDownload_img.setAdapter(adapter);
        rv_fileDownload_img.setLayoutManager(new LinearLayoutManager(FileDownloadActivity.this, RecyclerView.HORIZONTAL, false));
        
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
    }
    void initListeners(){

    }
}
