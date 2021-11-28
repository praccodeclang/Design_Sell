package com.taewon.shoppingmall.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.taewon.shoppingmall.R;

public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate (Bundle savedInstancestate) {
        super.onCreate(savedInstancestate);
        setContentView(R.layout.activity_splash);
        LinearLayout li_splash = findViewById(R.id.li_splash);
        Animation anim_fadein = AnimationUtils.loadAnimation(SplashActivity.this, R.anim.splash_anim);
        anim_fadein.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        li_splash.startAnimation(anim_fadein);
    }
}
