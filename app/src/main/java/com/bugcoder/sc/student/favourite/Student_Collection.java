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
import com.bugcoder.sc.student.Student_User;

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

public class Student_Collection extends Activity {

    private LinearLayout llContentView;

    private Handler handler;

    public void getThisCourseCollection(JSONObject CoreJson){
        OkHttpClient mOkHttpClient = new OkHttpClient();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        Student_User user = new Student_User();
        String url = user.IP+"/stu/getcollection";
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
        super.onCreate(savedInstanceState);
        setContentView(R.layout.student_activity_collection);
        // 获取当前课程名
        Intent intent = getIntent();
        String coursename = intent.getStringExtra("courseName");
        String stuid = intent.getStringExtra("stuId");
        System.out.println("courseName------------------------------------------------");
        System.out.println(coursename);
        System.out.println("stuid------------------------------------------------");
        System.out.println(stuid);

        JSONObject sendCourseName = new JSONObject();
        JSONObject tempCoreJson = new JSONObject();
        try {
            sendCourseName.put("courseName",coursename);
            sendCourseName.put("stuId",stuid);
            tempCoreJson.put("data",sendCourseName);
            tempCoreJson.put("check",1);
            System.out.println("Student_Collection CoreJson:::::::::::::::::::::::");
            System.out.println(tempCoreJson);
            getThisCourseCollection(tempCoreJson);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                addCourseCollection((String)msg.obj);
            }
        };

        // 需发送请求获取当前学生当前课程收藏数据
//        String req = "{\"data\":[{\"collectionId\":\"1\",\"description\":\"question1\",\"type\":\"question\"},{\"collectionId\":\"2\",\"description\":\"question2\",\"type\":\"question\"},{\"collectionId\":\"1\",\"description\":\"PPT1\",\"type\":\"PPT\"}],\"check\":1}";

    }

    public void addCourseCollection(String json){
        try {
            JSONObject getAllJson = new JSONObject(json);
            JSONArray dataJson = getAllJson.getJSONArray("data");

            final LayoutInflater inflater = LayoutInflater.from(this);
            // 获取需要被添加控件的布局
            final LinearLayout lin = (LinearLayout) findViewById(R.id.CollectionAll);

            // 将布局加入到当前布局中
            for(int i=0;i<dataJson.length();i++){
                // 获取需要添加的布局
                LinearLayout layout = (LinearLayout) inflater.inflate(
                        R.layout.student_onecollection, null).findViewById(R.id.AddOneCollection);
                lin.addView(layout);
            }

            LinearLayout AllCollections = (LinearLayout)findViewById(R.id.CollectionAll);
            for(int i=0;i<AllCollections.getChildCount();i++){
                View temp = AllCollections.getChildAt(i);
                if(temp instanceof LinearLayout){
                    LinearLayout oneCollection = (LinearLayout)AllCollections.getChildAt(i);
                    for(int j=0;j<oneCollection.getChildCount();j++){
                        View temp2 = oneCollection.getChildAt(j);
                        if(temp2 instanceof android.support.v7.widget.CardView){
                            android.support.v7.widget.CardView oneCollectionCard = (android.support.v7.widget.CardView) oneCollection.getChildAt(j);
                            JSONObject object = dataJson.getJSONObject(i);
                            final String tempdescription = object.getString("description");
                            final String temptype = object.getString("type");
                            String temptype2 = object.getString("type");
                            System.out.println("temptype-----------------------------");
                            System.out.println(temptype);

                            if(temptype2.equals("question")){
                                oneCollectionCard.setOnClickListener(new View.OnClickListener() {
                                    Intent intent = null;
                                    @Override
                                    public void onClick(View v) {
//                                        if(temptype=="question"){
                                        System.out.println("in question---------------------");
                                        intent = new Intent(Student_Collection.this, Student_ProblemCollectionInfo.class);
                                        intent.putExtra("description",tempdescription);
                                        intent.putExtra("type",temptype);
                                        startActivity(intent);
//                                        }else if(temptype=="PPT"){
//                                            System.out.println("in PPT---------------------");
//                                            intent = new Intent(Student_Collection.this, Student_PPTCollection.class);
//                                            intent.putExtra("description",tempdescription);
//                                            intent.putExtra("type",temptype);
//                                        }
                                    }
                                });
                            }

                            if(temptype2.equals("PPT")){
                                oneCollectionCard.setOnClickListener(new View.OnClickListener() {
                                    Intent intent = null;
                                    @Override
                                    public void onClick(View v) {
//                                        if(temptype=="question"){
//                                        System.out.println("in question---------------------");
//                                        intent = new Intent(Student_Collection.this, Student_ProblemCollectionInfo.class);
//                                        intent.putExtra("description",tempdescription);
//                                        intent.putExtra("type",temptype);
//                                        startActivity(intent);
//                                        }else if(temptype=="PPT"){
                                        System.out.println("in PPT---------------------");
                                        intent = new Intent(Student_Collection.this, Student_PPTCollection.class);
                                        intent.putExtra("description",tempdescription);
                                        intent.putExtra("type",temptype);
                                        startActivity(intent);
//                                        }
                                    }
                                });
                            }


                            for(int m=0;m<oneCollectionCard.getChildCount();m++){
                                View temp3 = oneCollectionCard.getChildAt(m);
                                if(temp3 instanceof LinearLayout){
                                    LinearLayout AllInfo = (LinearLayout) oneCollectionCard.getChildAt(m);
                                    TextView description = (TextView)AllInfo.getChildAt(0);
                                    TextView type = (TextView)AllInfo.getChildAt(1);

                                    description.setText(object.getString("description"));
                                    type.setText(object.getString("type"));
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
