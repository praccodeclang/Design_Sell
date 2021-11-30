package com.taewon.shoppingmall.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.InputType;
import android.util.Log;
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.taewon.shoppingmall.R;
import com.taewon.shoppingmall.util.PreferenceMgr;

import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {
    final private String autoLoginKey = "AutoLogin";
    Context context;

    FirebaseAuth mAuth;
    EditText et_idText;
    EditText et_pwText;
    ImageView iv_viewPassword;
    TextView tv_register;
    CheckBox cb_autoLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        init();
        autoLoginCheck();

        initViews();
        initListeners();

    }
    private void init(){
        context = this;
        mAuth = FirebaseAuth.getInstance();
    }
    private void initViews(){
        et_idText = findViewById(R.id.et_idText);
        et_pwText = findViewById(R.id.et_pwText);
        cb_autoLogin = findViewById(R.id.cb_autoLogin);
        iv_viewPassword = findViewById(R.id.iv_viewPassword);
        tv_register = findViewById(R.id.tv_register);
    }
    private void initListeners(){
        findViewById(R.id.btn_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //로그인
                login(et_idText.getText().toString(), et_pwText.getText().toString());
            }
        });
        tv_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
            }
        });
        iv_viewPassword.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        et_pwText.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                        et_pwText.invalidate();
                        break;
                    case MotionEvent.ACTION_MOVE:
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_BUTTON_RELEASE:
                        et_pwText.setInputType( InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        et_pwText.invalidate();
                        break;
                }
                return true;
            }
        });
    }

    private void autoLoginCheck(){
        if(PreferenceMgr.getString(context, autoLoginKey).equals("true")){
            String id = PreferenceMgr.getString(context, "id");
            String pw = PreferenceMgr.getString(context, "pw");
            login(id, pw);
            Log.d("자동로그인 ID:", id);
            Log.d("자동로그인 PW:", pw);
        }
    }

    private void login(String id, String pw){

        // 1. 빈칸 체크
        if(id.length() == 0 || pw.length() == 0){
            Toast.makeText(LoginActivity.this, "빈칸없이 입력해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }
        // 2. 비밀번호 8자리 이상.
        if(pw.length()<7){
            Toast.makeText(LoginActivity.this, "비밀번호는 8자리 이상입니다.", Toast.LENGTH_SHORT).show();
            return;
        }
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        if(!pattern.matcher(id).matches()){
            Toast.makeText(LoginActivity.this, "아이디는 이메일 형식입니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        // 3. 시작
        mAuth.signInWithEmailAndPassword(id, pw).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    if(cb_autoLogin.isChecked()){
                        PreferenceMgr.setString(context, autoLoginKey, "true");
                        PreferenceMgr.setString(context, "id", id);
                        PreferenceMgr.setString(context, "pw", pw);
                    }
                    mAuth.updateCurrentUser(task.getResult().getUser());
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
                else {
                    Toast.makeText(LoginActivity.this, "아이디 혹은 비밀번호가 틀렸습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


}