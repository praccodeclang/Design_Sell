package com.taewon.shoppingmall.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.taewon.shoppingmall.R;
import com.taewon.shoppingmall.item.User;

public class CashRefillActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseDatabase database;
    FirebaseStorage storage;
    TextView tv_refill_my_coin;
    Button btn_1000;
    Button btn_3000;
    Button btn_5000;
    Button btn_10000;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cash_refill);
        init();
        initViews();
        initListeners();
    }

    void init(){
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
    }
    void initViews(){
        tv_refill_my_coin = findViewById(R.id.tv_refill_my_coin);
        btn_1000 = findViewById(R.id.btn_1000);
        btn_3000 = findViewById(R.id.btn_3000);
        btn_5000 = findViewById(R.id.btn_5000);
        btn_10000 = findViewById(R.id.btn_10000);

        database.getReference("Users").child(mAuth.getCurrentUser().getUid()).get()
                .addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                    @Override
                    public void onSuccess(DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);
                        startCountAnimation(0, user.getCoin());
                    }
                });
    }
    void initListeners(){
        btn_1000.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(CashRefillActivity.this);
                builder.setTitle("코인 충전")
                        .setMessage("1000 코인을 충전하시겠습니까?")
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                refill(1000);
                            }
                        })
                        .setNegativeButton("취소", null)
                        .show();
            }
        });

        btn_3000.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(CashRefillActivity.this);
                builder.setTitle("코인 충전")
                        .setMessage("3000 코인을 충전하시겠습니까?")
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                refill(3000);
                            }
                        })
                        .setNegativeButton("취소", null)
                        .show();
            }
        });

        btn_5000.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(CashRefillActivity.this);
                builder.setTitle("코인 충전")
                        .setMessage("5000 코인을 충전하시겠습니까?")
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                refill(5000);
                            }
                        })
                        .setNegativeButton("취소", null)
                        .show();
            }
        });

        btn_10000.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(CashRefillActivity.this);
                builder.setTitle("코인 충전")
                        .setMessage("10000 코인을 충전하시겠습니까?")
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                refill(10000);
                            }
                        })
                        .setNegativeButton("취소", null)
                        .show();
            }
        });
    }

    private void refill(int coin){
        database.getReference("Users").child(mAuth.getCurrentUser().getUid()).get()
                .addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                    @Override
                    public void onSuccess(DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);
                        user.setCoin(user.getCoin() + coin);
                        dataSnapshot.getRef().setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(CashRefillActivity.this, "충전되었습니다.", Toast.LENGTH_SHORT).show();
                                finish();
                                overridePendingTransition(R.anim.slide_enter, R.anim.slide_exit);
                            }
                        });
                    }
                });
    }


    private void startCountAnimation(int start, int end) {
        ValueAnimator animator = ValueAnimator.ofInt(start, end); //0 is min number, 600 is max number
        animator.setDuration(2000); //Duration is in milliseconds
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
            }
        });
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                tv_refill_my_coin.setText(animation.getAnimatedValue().toString());
            }
        });
        animator.start();
    }
}
