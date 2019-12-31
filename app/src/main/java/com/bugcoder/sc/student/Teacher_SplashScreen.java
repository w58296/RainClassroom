package com.bugcoder.sc.student;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

public class Teacher_SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash_screen);
        Thread timerThread = new Thread() {
            public void run() {
                try {
                    sleep(1300);
                    Intent i = new Intent(Teacher_SplashScreen.this, Teacher_LoginScreen.class);
                    startActivity(i);
                    finish();
                } catch (Exception e) {
                }
            }
        };
        timerThread.start();
    }
}
