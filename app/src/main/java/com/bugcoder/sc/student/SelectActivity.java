package com.bugcoder.sc.student;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class SelectActivity extends AppCompatActivity {
    private Button studentbutton, teacherbutton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.student_activity_select);
        studentbutton = findViewById(R.id.studentbutton);
        teacherbutton = findViewById(R.id.teacherbutton);
        studentbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SelectActivity.this, Teacher_LoginScreen.class));
            }
        });
        teacherbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SelectActivity.this, Student_LoginScreen.class));
            }
        });
    }
}
