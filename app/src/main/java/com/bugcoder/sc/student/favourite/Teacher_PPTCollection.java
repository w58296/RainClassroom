package com.bugcoder.sc.student.favourite;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.bugcoder.sc.student.R;

public class Teacher_PPTCollection extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pptcollection_info);

        Intent intent = getIntent();
        String tempdescription = intent.getStringExtra("description");
        String temptype = intent.getStringExtra("type");
        System.out.println("description------------------------------------------------");
        System.out.println(tempdescription);
        System.out.println("type------------------------------------------------");
        System.out.println(temptype);
    }
}
