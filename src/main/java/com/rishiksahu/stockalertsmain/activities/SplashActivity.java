package com.rishiksahu.stockalertsmain.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.rishiksahu.stockalertsmain.MainActivity;
import com.rishiksahu.stockalertsmain.R;

public class SplashActivity extends AppCompatActivity {

    private ImageView logo, title;
    private Animation  bottomAnim;
    private FirebaseAuth firebaseAuth;
    private static int SPLASH_SCREEN = 1200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        firebaseAuth = FirebaseAuth.getInstance();

        logo = findViewById(R.id.logo);
        title = findViewById(R.id.titleD);

        bottomAnim = AnimationUtils.loadAnimation(this,R.anim.splash_bottom_animation);

        logo.setAnimation(bottomAnim);
        title.setAnimation(bottomAnim);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                FirebaseUser currentUser = firebaseAuth.getCurrentUser();

                if (getIntent().getStringExtra("msg_title") == null) {
                    if (currentUser == null) {
                        Intent disclaimerIntent = new Intent(SplashActivity.this, DisclaimerActivity.class);
                        startActivity(disclaimerIntent);
                        finish();

                    } else {
                        Intent mainintent = new Intent(SplashActivity.this, MainActivity.class);
                        startActivity(mainintent);
                        finish();

                    }
                }else {
                    //Intent is not empty
                    Intent noti = new Intent(SplashActivity.this, NotificationDisplayActivity.class);
                    noti.putExtra("msg_title", getIntent().getStringArrayExtra("msg_title"));
                    noti.putExtra("msg_body", getIntent().getStringArrayExtra("msg_body"));
                    startActivity(noti);
                    finish();
                }
            }
        },SPLASH_SCREEN);
    }


}