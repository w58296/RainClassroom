package com.bugcoder.sc.student.favourite;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bugcoder.sc.student.R;
import com.bugcoder.sc.student.Teacher_User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Teacher_CourseListForCollection extends Activity {

    private LinearLayout llContentView;
    private Handler handler;
    private String stuId;

    public void getAllCourseList(JSONObject CoreJson){
        OkHttpClient mOkHttpClient = new OkHttpClient();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        Intent temp = getIntent();
        stuId = temp.getStringExtra("stuId");

        Teacher_User user = new Teacher_User();
        String url = user.IP+"/stu/getCourse";
        Request request = new Request.Builder()
                .url(url)
                .post(RequestBody.create(JSON, CoreJson.toString()))
                .build();

        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                System.out.println("failure");
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                System.out.println("success");
                String json = response.body().string();
                System.out.println("JJJJJJJJJJJJJJJJJJJJJJJJJJJJJJ");
                System.out.println(json);

                Message msg = handler.obtainMessage();
                msg.what = 1;
                msg.obj = json;
                handler.sendMessage(msg);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        String req="{\"data\":[{\"courseName\":\"course1\",\"information\":\"this is course1\",\"teacherName\":\"teacher1\"},{ \"courseName\":\"course2\",\"information\":\"this is course2\",\"teacherName\":\"teacher2\"},{\"courseName\":\"course3\",\"information\":\"this is course3\",\"teacherName\":\"teacher3\"}],\"check\":1}";
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_list_for_collection);

        JSONObject tempdataJson = new JSONObject();
        JSONObject tempcoreJson = new JSONObject();

        try {
            //需要获取上一个activity传入的学生ID
            tempdataJson.put("stuId","17221294");
            tempcoreJson.put("data",tempdataJson);
            tempcoreJson.put("check",1);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        getAllCourseList(tempcoreJson);

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                addCourseList((String)msg.obj);
            }
        };
}

public void addCourseList(String json){
    try {
        JSONObject getAllJson = new JSONObject(json);
        JSONArray courseJson = getAllJson.getJSONArray("data");


        final LayoutInflater inflater = LayoutInflater.from(this);
        // 获取需要被添加控件的布局
        final LinearLayout lin = (LinearLayout) findViewById(R.id.CollectionCourseListAll);

        // 将布局加入到当前布局中
        for(int i=0;i<courseJson.length();i++){
            // 获取需要添加的布局
            LinearLayout layout = (LinearLayout) inflater.inflate(
                    R.layout.collectioncourselist, null).findViewById(R.id.AddOneCourse);
            lin.addView(layout);
        }

//        lin = (LinearLayout)findViewById(R.id.CollectionCourseListAll);
        for(int i=0;i<lin.getChildCount();i++){
            View temp = lin.getChildAt(i);
            if(temp instanceof LinearLayout){
                System.out.println("rrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrr");
                LinearLayout oneCourse = (LinearLayout)lin.getChildAt(i);
                for(int j=0;j<oneCourse.getChildCount();j++){
                    View temp2 = oneCourse.getChildAt(j);
                    if(temp2 instanceof android.support.v7.widget.CardView){
//                         oneCourseCard = new android.support.v7.widget.CardView(this);
                        android.support.v7.widget.CardView oneCourseCard = (android.support.v7.widget.CardView) oneCourse.getChildAt(j);
                        JSONObject object = courseJson.getJSONObject(i);
//                        final String tempcoursename = object.getString("courseName");
                        System.out.println("ttttttttttttttttttttttttttttttttttttttttttttt");
                        System.out.println(oneCourseCard.getClass());
                        oneCourseCard.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                System.out.println("clickkkkkkkkkkkkkkkkkkkkkkkkkkkk");
                                Intent intent = new Intent(getApplicationContext(), Teacher_Collection.class);
//                                intent.putExtra("courseName",tempcoursename);
                                intent.putExtra("stuId",stuId);
                                startActivity(intent);
                            }
                        });

                        for(int n=0;n<oneCourseCard.getChildCount();n++){
                            View temp3=oneCourseCard.getChildAt(n);
                            if(temp3 instanceof LinearLayout){
                                LinearLayout insideCourseCard = (LinearLayout)oneCourseCard.getChildAt(n);
                                TextView courseName = (TextView)insideCourseCard.getChildAt(0);
                                TextView courseDesc = (TextView)insideCourseCard.getChildAt(1);
                                TextView courseTeacher = (TextView)insideCourseCard.getChildAt(2);
                                courseName.setText(object.getString("courseName"));
                                courseDesc.setText(object.getString("information"));
                                courseTeacher.setText(object.getString("teacherName"));
                            }
                        }

                    }
                }
            }
        }


    } catch (JSONException e) {
        e.printStackTrace();
    }
}
}
