package com.bugcoder.sc.student.favourite;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.bugcoder.sc.student.R;
import com.bugcoder.sc.student.Teacher_User;

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

public class Teacher_ProblemCollectionInfo extends Activity {

    private LinearLayout llContentView;

    private Handler handler;

    public void getThisCourseCollectionDetail(JSONObject CoreJson){
        OkHttpClient mOkHttpClient = new OkHttpClient();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        Teacher_User user = new Teacher_User();
        String url = user.IP+"/stu/getcollection/content";
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
        setContentView(R.layout.activity_problem_collection_info);

        Intent intent = getIntent();
        String tempdescription = intent.getStringExtra("description");
        String temptype = intent.getStringExtra("type");
        System.out.println("description------------------------------------------------");
        System.out.println(tempdescription);
        System.out.println("type------------------------------------------------");
        System.out.println(temptype);

        JSONObject tempdataJson = new JSONObject();
        JSONObject tempcoreJson = new JSONObject();
        try {
            tempdataJson.put("description",tempdescription);
            tempdataJson.put("type",temptype);
            tempcoreJson.put("data",tempdataJson);
            tempcoreJson.put("check",1);
            System.out.println("ProblemCollection___________________________________________");
            System.out.println(tempcoreJson);
            getThisCourseCollectionDetail(tempcoreJson);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                addCollectionDetail((String)msg.obj);
            }
        };


        // 发送请求

//        String req = "{\"data\":{\"description\":\"question1\", \"options\":\"A:1,B:2,C:3,D:4\",\"answer\":\"B\"},\"check\":1}";


    }

    public void addCollectionDetail(String json){
        final LayoutInflater inflater = LayoutInflater.from(this);
        // 获取需要被添加控件的布局
        final LinearLayout lin = (LinearLayout) findViewById(R.id.ProblemCollectionVerticalAll);

        // 获取需要添加的布局
        LinearLayout layout = (LinearLayout) inflater.inflate(
                R.layout.questionanswercard, null).findViewById(R.id.AddExistQuestion);
        lin.addView(layout);

        try {
            JSONObject getAllJson = new JSONObject(json);
            JSONObject oneProblemCollectionData = getAllJson.getJSONObject("data");
            LinearLayout problemVerticalAll = (LinearLayout) findViewById(R.id.ProblemCollectionVerticalAll);
            for(int i=0;i<problemVerticalAll.getChildCount();i++){
                View temp = problemVerticalAll.getChildAt(i);
                if(temp instanceof LinearLayout){
                    LinearLayout oneQuestion = (LinearLayout)problemVerticalAll.getChildAt(i);
                    for(int j=0;j<oneQuestion.getChildCount();j++){
                        View temp2 = oneQuestion.getChildAt(j);
                        if(temp2 instanceof android.support.v7.widget.CardView){
                            android.support.v7.widget.CardView oneCard = (android.support.v7.widget.CardView)oneQuestion.getChildAt(j);
                            for(int m=0;m<oneCard.getChildCount();m++){
                                View temp3 = oneCard.getChildAt(m);
                                if(temp3 instanceof LinearLayout){
                                    LinearLayout questionContent = (LinearLayout) oneCard.getChildAt(m);
                                    TextView questionHead = (TextView) questionContent.getChildAt(1);
                                    RadioGroup options = (RadioGroup) questionContent.getChildAt(2);
                                    RadioButton optionA = (RadioButton) options.getChildAt(0);
                                    RadioButton optionB = (RadioButton) options.getChildAt(1);
                                    RadioButton optionC = (RadioButton) options.getChildAt(2);
                                    RadioButton optionD = (RadioButton) options.getChildAt(3);
                                    questionHead.setText(oneProblemCollectionData.getString("description"));
                                    String optionstr = oneProblemCollectionData.getString("options");
                                    String answerstr = oneProblemCollectionData.getString("answer");
                                    String[] fourOptions = optionstr.split(",");

                                    System.out.println("answer:::::::::::::::::::::::::::::::");
                                    System.out.println(answerstr);

                                    optionA.setText(fourOptions[0]);
                                    optionB.setText(fourOptions[1]);
                                    optionC.setText(fourOptions[2]);
                                    optionD.setText(fourOptions[3]);

                                    if(answerstr.equals("A")){
                                        optionA.setChecked(true);
                                    }
                                    if(answerstr.equals("B")){
                                        optionB.setChecked(true);
                                    }
                                    if(answerstr.equals("C")){
                                        optionC.setChecked(true);
                                    }
                                    if(answerstr.equals("D")){
                                        optionD.setChecked(true);
                                    }

                                    optionA.setClickable(false);
                                    optionB.setClickable(false);
                                    optionC.setClickable(false);
                                    optionD.setClickable(false);
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
