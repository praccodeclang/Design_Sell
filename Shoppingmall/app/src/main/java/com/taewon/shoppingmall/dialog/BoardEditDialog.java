package com.taewon.shoppingmall.dialog;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.taewon.shoppingmall.R;
import com.taewon.shoppingmall.item.BoardItem;

public class BoardEditDialog extends BottomSheetDialog {

    Context context;
    LinearLayout post_edit_wrap_layout;
    LinearLayout post_edit_revise;
    LinearLayout post_edit_delete;
    LinearLayout post_edit_report;
    BoardItem instance;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    public BoardEditDialog(@NonNull Context context, BoardItem instance) {
        super(context);
        this.context = context;
        this.instance = instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.board_edit_dialog);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        initViews();
        initListener();
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

    private void initListener(){
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
                Toast.makeText(context, "삭제하기", Toast.LENGTH_SHORT).show();
                dismiss();
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
}
