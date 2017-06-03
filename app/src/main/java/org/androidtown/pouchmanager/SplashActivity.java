package org.androidtown.pouchmanager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SplashActivity extends AppCompatActivity {

    private SingletonUserInfo mSingletonUserInfo = SingletonUserInfo.getInstance();
    private final int SPLASH_DISPLAY_LENGTH = 2000;
    private SharedPreferences loginPreferences;
    private SharedPreferences.Editor loginPrefsEditor;
    private Boolean saveLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        loginPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        loginPrefsEditor = loginPreferences.edit();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // TODO 자동 로그인 기능이 있기 때문에 로그아웃 기능 구현 필요
                saveLogin = loginPreferences.getBoolean("saveLogin", false);
                if (saveLogin == true) {
                    mSingletonUserInfo.setUserId(loginPreferences.getString("id", null));
                    if (mSingletonUserInfo.getUserId() == null) {
                        // 자동 로그인 ID가 이상함
                    } else {
                        // 자동 로그인
                        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                        startActivity(intent);

                        SplashActivity.this.finish();
                    }
                } else {
                    Intent loginIntent = new Intent(SplashActivity.this, LoginActivity.class);
                    SplashActivity.this.startActivity(loginIntent);
                    SplashActivity.this.finish();
                }
            }
        }, SPLASH_DISPLAY_LENGTH);
    }
}
