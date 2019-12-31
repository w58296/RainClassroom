package com.bugcoder.sc.student;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

public class Student_SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.student_activity_splash_screen);
        Thread timerThread = new Thread() {
            public void run() {
                try {
                    sleep(1300);
                    Intent i = new Intent(Student_SplashScreen.this, Student_LoginScreen.class);
                    startActivity(i);
                    finish();
                } catch (Exception e) {
                }
            }
        };
        timerThread.start();
    }
}
