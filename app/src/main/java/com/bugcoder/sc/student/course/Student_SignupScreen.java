package com.bugcoder.sc.student.course;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.bugcoder.sc.student.R;

public class Student_SignupScreen extends AppCompatActivity implements View.OnClickListener {
    Button btn_signUP;
    TextView tv_signIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.student_activity_signup_screen);

        btn_signUP = findViewById(R.id.btn_sign_up);
        btn_signUP.setOnClickListener(this);

        tv_signIn = findViewById(R.id.tv_sign_in);
        tv_signIn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.tv_sign_in || view.getId() == R.id.btn_sign_up) {
            finish();
        }
    }
}
