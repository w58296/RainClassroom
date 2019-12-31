package com.bugcoder.sc.student.course;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;


import com.bugcoder.sc.student.R;
import com.bugcoder.sc.student.Student_User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import cc.ibooker.zcountdownviewlib.CountDownView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Student_TestPaperAnswering extends Activity {

    private LinearLayout llContentView;
    private Handler handler;
    private String info;

    public void getPaper() {
        OkHttpClient mOkHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://192.168.43.157:8080/stu/paper/getPaper")
                .build();
        Call call = mOkHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("message", "failure");
                Log.d("error message:", e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String json = response.body().string();
                System.out.println("JJJJJJJJJJJJJJJJJJJJJJJJJJJJJJ");
                System.out.println(json);

                Message msg = handler.obtainMessage();
                msg.what = 1;
                msg.obj = json;
                handler.sendMessage(msg);

                Looper.prepare();
                Toast.makeText(getApplicationContext(), "testtesttest", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void sendPaperAnswer(JSONObject CoreJson){
        OkHttpClient mOkHttpClient = new OkHttpClient();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        Student_User user = new Student_User();
        String url = user.IP+"/stu/paper/sendAnswer";
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
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.student_activity_test_paper_answering);

        getPaper();

//        String req = "{\"data\":{\"paperName\":\"Test A\",\"content\":[{\"description\":\"456745\",\"options\":\"A:4567,B:235,C:2345,D:235\",\"answer\":\"A\"},{\"description\":\"asgfasdf\",\"options\":\"A:asdfadsf,B:werterwt,C:wertwert,D:erytrtey\",\"answer\":\"C\"},{\"description\":\"34564365 wertewrt\",\"options\":\"A:wertwert 2353 wert,B:wetwert wertwert,C:435254 2345235,D:wertwert  2345345\",\"answer\":\"D\"}]},\"check\":1}";

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                addPaperQuestions((String)msg.obj);
            }
        };

        final CountDownView countdownView = findViewById(R.id.countdownView);

// 基本属性设置
        countdownView.setCountTime(20) // 设置倒计时时间戳
//                .setHourTvBackgroundRes(R.mipmap.panicbuy_time)
                .setHourTvTextColorHex("#FFFFFF")
                .setHourTvGravity(CountDownView.CountDownViewGravity.GRAVITY_CENTER)
                .setHourTvTextSize(21)

                .setHourColonTvBackgroundColorHex("#00FFFFFF")
                .setHourColonTvSize(18, 0)
                .setHourColonTvTextColorHex("#FF7198")
                .setHourColonTvGravity(CountDownView.CountDownViewGravity.GRAVITY_CENTER)
                .setHourColonTvTextSize(21)

//                .setMinuteTvBackgroundRes(R.mipmap.panicbuy_time)
                .setMinuteTvTextColorHex("#FFFFFF")
                .setMinuteTvTextSize(21)

                .setMinuteColonTvSize(18, 0)
                .setMinuteColonTvTextColorHex("#FF7198")
                .setMinuteColonTvTextSize(21)

//                .setSecondTvBackgroundRes(R.mipmap.panicbuy_time)
                .setSecondTvTextColorHex("#FFFFFF")
                .setSecondTvTextSize(21)

//      .setTimeTvWH(18, 40)
//      .setColonTvSize(30)

                // 开启倒计时
                .startCountDown()

                // 设置倒计时结束监听
                .setCountDownEndListener(new CountDownView.CountDownEndListener() {
                    @Override
                    public void onCountDownEnd() {
                        Toast.makeText(Student_TestPaperAnswering.this, "倒计时结束", Toast.LENGTH_SHORT).show();
                    }
                });

// 测试暂停倒计时
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                // 暂停倒计时
//                countdownView.pauseCountDown();
//            }
//        }, 5000);

// 测试停止倒计时
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                // 停止倒计时
//                countdownView.stopCountDown();
//            }
//        }, 15000);


        FloatingActionButton SubmitAnswerButton = (FloatingActionButton) findViewById(R.id.floatingActionButton3);
        SubmitAnswerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject CoreJson = new JSONObject();
                JSONObject DataJson = new JSONObject();
                JSONArray answers = new JSONArray();

                LinearLayout AllQuestions = (LinearLayout)findViewById(R.id.AnswerPaperVerticalAll);
                for(int i=0;i<AllQuestions.getChildCount();i++){
                    View temp = AllQuestions.getChildAt(i);
                    if(temp instanceof LinearLayout){
                        LinearLayout oneQuestion = (LinearLayout)AllQuestions.getChildAt(i);
                        for(int j=0;j<oneQuestion.getChildCount();j++){
                            View temp2 = oneQuestion.getChildAt(j);
                            if(temp2 instanceof android.support.v7.widget.CardView){
                                android.support.v7.widget.CardView oneCard = (android.support.v7.widget.CardView)oneQuestion.getChildAt(j);
                                for(int m=0;m<oneCard.getChildCount();m++){
                                    JSONObject oneQueAnswer = new JSONObject();
                                    View temp3 = oneCard.getChildAt(m);
                                    if(temp3 instanceof LinearLayout){
                                        LinearLayout questionContent = (LinearLayout) oneCard.getChildAt(m);
                                        TextView questionHead = (TextView) questionContent.getChildAt(1);
                                        RadioGroup options = (RadioGroup) questionContent.getChildAt(2);

                                        for(int o=0;o<options.getChildCount();o++){
                                            View temp4 = options.getChildAt(o);
                                            if(temp4 instanceof RadioButton){
                                                RadioButton radioButton = (RadioButton) options.getChildAt(o);
                                                if(radioButton.isChecked()){
                                                    String stuAnswer = "";
                                                    if(o==0)
                                                        stuAnswer += "A";
                                                    else if(o==1)
                                                        stuAnswer += "B";
                                                    else if(o==2)
                                                        stuAnswer += "C";
                                                    else if(o==3)
                                                        stuAnswer += "D";
                                                    try {
                                                        oneQueAnswer.put("answer",stuAnswer);
                                                        answers.put(oneQueAnswer);
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                try {
                    DataJson.put("paperName", "Test 1");
                    DataJson.put("answers", answers);

                    CoreJson.put("data", DataJson);
                    CoreJson.put("check", 1);
                    sendPaperAnswer(CoreJson);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                System.out.println("CoreJson is:-----------------------------------------");
                System.out.println(CoreJson);

            }
        });
    }

    public void addPaperQuestions(String json){
        try {
            JSONObject getAllJson = new JSONObject(json);
            JSONObject dataJson = getAllJson.getJSONObject("data");
            System.out.println("dataJson:");
            System.out.println(dataJson);

            String paperName = dataJson.getString("paperName");
            System.out.println("paperName:");
            System.out.println(paperName);

            JSONArray contentJson = dataJson.getJSONArray("content");
            System.out.println("contentJson:");
            System.out.println(contentJson);
            System.out.println("contentJson length:");
            System.out.println(contentJson.length());

            llContentView = findViewById(R.id.AnswerPaperVerticalAll);

            final LayoutInflater inflater = getLayoutInflater();
            // 获取需要被添加控件的布局
            final LinearLayout lin = (LinearLayout) findViewById(R.id.AnswerPaperVerticalAll);

            // 将布局加入到当前布局中
            for(int i=0;i<contentJson.length();i++){
                // 获取需要添加的布局
                LinearLayout layout = (LinearLayout) inflater.inflate(
                        R.layout.student_questionanswercard, null).findViewById(R.id.AddExistQuestion);
                lin.addView(layout);
            }

            LinearLayout AllQuestions = (LinearLayout)findViewById(R.id.AnswerPaperVerticalAll);
            for(int i=1;i<contentJson.length()+1;i++){
                JSONObject object = contentJson.getJSONObject(i-1);
                View temp = AllQuestions.getChildAt(i);
                if(temp instanceof LinearLayout){
                    LinearLayout oneQuestion = (LinearLayout)AllQuestions.getChildAt(i);
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

                                    questionHead.setText(object.getString("description"));
                                    String optionstr = object.getString("options");
                                    String[] fourOptions = optionstr.split(",");

                                    optionA.setText(fourOptions[0]);
                                    optionB.setText(fourOptions[1]);
                                    optionC.setText(fourOptions[2]);
                                    optionD.setText(fourOptions[3]);
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
