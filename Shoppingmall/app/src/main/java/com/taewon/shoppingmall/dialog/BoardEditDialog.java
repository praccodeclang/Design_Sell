package com.taewon.shoppingmall.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.taewon.shoppingmall.R;
import com.taewon.shoppingmall.adapter.BoardRecyclerAdapter;
import com.taewon.shoppingmall.item.BoardItem;

public class BoardEditDialog extends BottomSheetDialog {
    Context context;
    LinearLayout post_edit_wrap_layout;
    LinearLayout post_edit_revise;
    LinearLayout post_edit_delete;
    LinearLayout post_edit_report;
    FirebaseAuth mAuth;
    FirebaseDatabase database;
    FirebaseStorage storage;
    BoardItem instance;
    private BoardEditDialog.DialogClickListener listener;

    public BoardEditDialog(@NonNull Context context, BoardItem instance, BoardEditDialog.DialogClickListener listener) {
        super(context);
        this.context = context;
        this.instance = instance;
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.board_edit_dialog);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        initViews();
        initListeners();
        if(!(mAuth.getCurrentUser().getUid().equals(instance.getUid()))){
            post_edit_revise.setVisibility(View.GONE);
            post_edit_delete.setVisibility(View.GONE);
        }
    }

    private void initViews(){
        post_edit_wrap_layout = findViewById(R.id.post_edit_wrap_layout);
        post_edit_revise = findViewById(R.id.post_edit_revise);
        post_edit_delete = findViewById(R.id.post_edit_delete);
        post_edit_report = findViewById(R.id.post_edit_report);
    }

    private void initListeners(){
        post_edit_revise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "수정하기", Toast.LENGTH_SHORT).show();
                dismiss();
            }
        });
        post_edit_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                dismiss();
                builder.setTitle("게시글 삭제")
                        .setMessage("정말로 삭제하시겠습니까?")
                        .setPositiveButton("확인", new OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                DatabaseReference databaseRef = database.getReference("Board");
                                databaseRef.child(instance.getBoardID())
                                        .setValue(null).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        StorageReference storageRef = storage.getReference("Board");
                                        storageRef.child(instance.getBoardID()).listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
                                            @Override
                                            public void onSuccess(ListResult listResult) {
                                                for(StorageReference result : listResult.getItems()){
                                                    result.delete();
                                                }
                                                listener.onDelete("OK");
                                                Toast.makeText(context, "삭제되었습니다.", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(context, "오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        })
                        .setNegativeButton("취소", null)
                        .show();
            }
        });
        post_edit_report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "신고하기", Toast.LENGTH_SHORT).show();
                dismiss();
            }
        });
    }

    public interface DialogClickListener{
        void onDelete(String result);
    }
}
