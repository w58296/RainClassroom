package com.bugcoder.sc.student.course;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

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

public class Teacher_TestQuestioinAnswering extends AppCompatActivity {

    private LinearLayout llContentView;
    private Handler handler;
    private String qHead;
    Teacher_User user = new Teacher_User();

    public void getQuestion() {
        System.out.println("getQuestion::::::::::::::::::::::::::::::;");
        OkHttpClient mOkHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .url(user.IP+"/stu/getquestion")
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

    public void sendQuestionAnswer(JSONObject CoreJson){
        System.out.println("SendQuestion+++++++++++++++++++++++++++++++++++++");
        OkHttpClient mOkHttpClient = new OkHttpClient();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");


        String url = user.IP+"/stu/sendAnswer";
        Request request = new Request.Builder()
                .url(url)
                .post(RequestBody.create(JSON, CoreJson.toString()))
                .build();

        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                System.out.println("failure&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&");
                System.out.println(e.toString());
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
        setContentView(R.layout.activity_test_questioin_answering);

//        String req = "{\"data\":{\"description\":\"64365\",\"options\":\"A:3465346,B:34653465,C:346534634,D:536436436\",\"answer\":\"C\"},\"check\":1}";
        getQuestion();

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                AddQuestion((String)msg.obj);
                try {
                    JSONObject temp = new JSONObject((String)msg.obj);
                    JSONObject temp2 = temp.getJSONObject("data");
                    qHead = temp2.getString("description");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };


        FloatingActionButton SubmitAnswerButton = (FloatingActionButton) findViewById(R.id.floatingActionButton5);
        SubmitAnswerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject CoreJson = new JSONObject();
                JSONObject DataJson = new JSONObject();
                JSONObject oneQueAnswer = new JSONObject();
                JSONArray answers = new JSONArray();

                LinearLayout AllQuestions = (LinearLayout)findViewById(R.id.AnswerQuestionVerticalAll);
                for(int i=0;i<AllQuestions.getChildCount();i++){
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
                                                        oneQueAnswer.put("description",qHead);
//                                                        answers.put(oneQueAnswer);
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
                    CoreJson.put("data", oneQueAnswer);
                    CoreJson.put("check", 0);
                    sendQuestionAnswer(CoreJson);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                System.out.println("CoreJson is:-----------------------------------------");
                System.out.println(CoreJson);

            }
        });
    }

    public void AddQuestion(String json){
        try {
            JSONObject getAllJson = new JSONObject(json);
            JSONObject dataJson = getAllJson.getJSONObject("data");
            System.out.println("dataJson:");
            System.out.println(dataJson);

            llContentView = findViewById(R.id.AnswerQuestionVerticalAll);

            final LayoutInflater inflater = LayoutInflater.from(this);
            // 获取需要被添加控件的布局
            final LinearLayout lin = (LinearLayout) findViewById(R.id.AnswerQuestionVerticalAll);

            // 将布局加入到当前布局中
//            for(int i=0;i<contentJson.length();i++){
            // 获取需要添加的布局
            LinearLayout layout = (LinearLayout) inflater.inflate(
                    R.layout.questionanswercard, null).findViewById(R.id.AddExistQuestion);
            lin.addView(layout);
//            }

            LinearLayout AllQuestions = (LinearLayout)findViewById(R.id.AnswerQuestionVerticalAll);
//            for(int i=0;i<contentJson.length();i++){
            View temp = AllQuestions.getChildAt(1);
            if(temp instanceof LinearLayout){
                LinearLayout oneQuestion = (LinearLayout)AllQuestions.getChildAt(1);
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
//                                    JSONObject object = contentJson.getJSONObject(i);
                                questionHead.setText(dataJson.getString("description"));
                                String optionstr = dataJson.getString("options");
                                String[] fourOptions = optionstr.split(",");
                                System.out.println("in this question______________________________________");

                                optionA.setText(fourOptions[0]);
                                optionB.setText(fourOptions[1]);
                                optionC.setText(fourOptions[2]);
                                optionD.setText(fourOptions[3]);
                            }
                        }
                    }
                }
            }
//            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
