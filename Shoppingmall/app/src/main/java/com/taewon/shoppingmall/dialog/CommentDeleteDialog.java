package com.taewon.shoppingmall.dialog;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.taewon.shoppingmall.R;
import com.taewon.shoppingmall.item.BoardCommentItem;

public class CommentDeleteDialog extends Dialog {

    Context context;
    BoardCommentItem instance;
    FirebaseDatabase database;
    private DialogClickListener listener;

    public CommentDeleteDialog(@NonNull Context context, BoardCommentItem instance, DialogClickListener listener) {
        super(context);
        this.context = context;
        this.listener = listener;
        this.instance = instance;
        database = FirebaseDatabase.getInstance();
    }

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_comment_delete);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        findViewById(R.id.tv_delete_comment_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                database.getReference("Board").child(instance.getBoardID()).child("comments").child(instance.getCommentID())
                        .setValue(null)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                dismiss();
                                Toast.makeText(context, "삭제되었습니다.", Toast.LENGTH_SHORT).show();
                                listener.onDelete();
                            }
                        });
            }
        });

        findViewById(R.id.tv_delete_comment_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
    }

    void init(){

    }

    void initViews(){

    }

    void initListeners(){

    }


    public interface DialogClickListener{
        void onDelete();
    }
}
