package com.taewon.shoppingmall.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.taewon.shoppingmall.R;
import com.taewon.shoppingmall.User;
import com.taewon.shoppingmall.dialog.LottieLoadingDialog;
import com.taewon.shoppingmall.util.BoardDateComparator;
import com.taewon.shoppingmall.util.BoardStarCountComparator;

import java.util.ArrayList;
import java.util.Collections;

public class RegisterActivity extends AppCompatActivity {
    FirebaseAuth mAuth;
    FirebaseDatabase database;
    LottieLoadingDialog loadingDialog;
    Spinner sp_emailSpinner;
    TextView tv_passwordGuide;
    EditText et_registerUserName;
    EditText et_registerEmail;
    EditText et_registerPassword;
    EditText et_registerPasswordAgain;
    EditText et_registerPhone;
    RadioGroup rg_isDesignerGroup;
    RadioButton rd_yesDesigner;
    RadioButton rd_noDesigner;
    Button btn_register;
    Button btn_backpressed;

     boolean isMatchedPassword = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        init();
        initViews();
        initListeners();
    }



    private void init(){
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
    }

    private void initViews(){
        tv_passwordGuide = findViewById(R.id.tv_passwordGuide);
        et_registerUserName = findViewById(R.id.et_registerUserName);
        et_registerEmail = findViewById(R.id.et_registerEmail);
        et_registerPassword = findViewById(R.id.et_registerPassword);
        et_registerPasswordAgain = findViewById(R.id.et_registerPasswordAgain);
        et_registerPhone = findViewById(R.id.et_registerPhone);
        rg_isDesignerGroup = findViewById(R.id.rg_isDesignerGroup);
        rd_yesDesigner = findViewById(R.id.rd_yesDesigner);
        rd_noDesigner = findViewById(R.id.rd_noDesigner);
        btn_register = findViewById(R.id.btn_register);
        btn_backpressed = findViewById(R.id.btn_backpressed);
        sp_emailSpinner = (Spinner) findViewById(R.id.sp_emailSpinner);
        ArrayAdapter adapter = ArrayAdapter.createFromResource(this, R.array.register_spinner_items, R.layout.support_simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        sp_emailSpinner.setAdapter(adapter);
        sp_emailSpinner.setSelection(0);
        loadingDialog = new LottieLoadingDialog(RegisterActivity.this);
    }

    private void initListeners(){
        tv_passwordGuide.setVisibility(View.GONE);
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingDialog.show();
                String userName = et_registerUserName.getText().toString().trim();
                String userEmail = et_registerEmail.getText().toString().trim();
                String userPassword = et_registerPassword.getText().toString();
                String userPhone = et_registerPhone.getText().toString().trim();
                String spinnerString = sp_emailSpinner.getSelectedItem().toString();
                boolean isConditionOk = checkRegisterCondition(userName, userEmail, userPhone, spinnerString);
                if(isConditionOk){
                    registerUser(userEmail+"@"+spinnerString, userPassword);
                }
                else{
                    loadingDialog.dismiss();
                }
            }
        });
        btn_backpressed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        et_registerPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkPasswordMatch();
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });
        et_registerPasswordAgain.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkPasswordMatch();
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });
    }
    private void checkPasswordMatch(){
        String pass = et_registerPassword.getText().toString();
        String passAgain = et_registerPasswordAgain.getText().toString();
        tv_passwordGuide.setVisibility(View.VISIBLE);
        if(pass.trim().equals("")){
            return;
        }
        if(pass.equals(passAgain)){
            tv_passwordGuide.setText("비밀번호가 일치합니다.");
            tv_passwordGuide.setTextColor(Color.GREEN);
            isMatchedPassword = true;
        }
        else{
            tv_passwordGuide.setText("비밀번호가 일치하지 않습니다.");
            tv_passwordGuide.setTextColor(Color.RED);
            isMatchedPassword = false;
        }
    }
    private void registerUser(String email, String password){
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    //가입완료
                    FirebaseUser registerUser= task.getResult().getUser();
                    User userInstance = new User();
                    userInstance.setUid(registerUser.getUid());
                    userInstance.setUsername(et_registerUserName.getText().toString());
                    userInstance.setEmail(registerUser.getEmail());
                    userInstance.setPhone(et_registerPhone.getText().toString());
                    userInstance.setIsDesigner(rd_yesDesigner.isChecked());
                    userInstance.setPhotoUrl("Profile/"+userInstance.getUid()+"/profile.png");

                    addUserOnDataBase(userInstance);
                }
                else{
                    loadingDialog.dismiss();
                    //이미 존재
                    Toast.makeText(RegisterActivity.this, "이미 존재하는 이메일입니다.", Toast.LENGTH_SHORT).show();
                }
            }
        })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                loadingDialog.dismiss();
                Toast.makeText(RegisterActivity.this, "가입에 실패했습니다. 다시 시도해보세요.", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void addUserOnDataBase(User user){
        database.getReference("Users").child(user.getUid()).setValue(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(RegisterActivity.this, "가입되었습니다.", Toast.LENGTH_SHORT).show();
                        loadingDialog.dismiss();
                        onBackPressed();
                    }
                });

    }

    private boolean checkRegisterCondition(
            String userName, String userEmail, String userPhone, String spinnerString)
    {
        if(userName.equals("")){
            Toast.makeText(RegisterActivity.this, "이름을 입력해주세요.", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(userEmail.equals("") || spinnerString.equals("")){
            Toast.makeText(RegisterActivity.this, "이메일을 올바르게 입력해주세요.", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(!isMatchedPassword){
            Toast.makeText(RegisterActivity.this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(userPhone.equals("")){
            Toast.makeText(RegisterActivity.this, "전화번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(!rd_noDesigner.isChecked() && !rd_yesDesigner.isChecked()){
            Toast.makeText(RegisterActivity.this, "디자이너 여부를 선택해주세요.", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.slide_enter, R.anim.slide_exit);
    }
}