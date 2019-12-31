package com.bugcoder.sc.student.course;

import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

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

public class Student_TestQuestionRelease extends AppCompatActivity {

    // 外围的LinearLayout容器
    private LinearLayout llContentView;
    private Handler handler;
    Student_User user = new Student_User();

    public void sendQuestion(JSONObject CoreJson){
        OkHttpClient mOkHttpClient = new OkHttpClient();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        Request request = new Request.Builder()
                .url(user.IP+"/tea/releasequestion")
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
                System.out.println(response.body().string());
            }
        });
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.student_activity_test_question_release);

        FloatingActionButton sendQuestionButton = (FloatingActionButton) findViewById(R.id.floatingActionButton4);
        sendQuestionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONArray jsonArray=new JSONArray();
                JSONObject CoreJson = new JSONObject();
                JSONObject DataJson = new JSONObject();
                TextView questionName = (TextView) findViewById(R.id.editText6);
                LinearLayout AllQuestions = (LinearLayout)findViewById(R.id.questionVerticalAll);
//                int Count = AllQuestions.getChildCount();
                for (int i = 0; i < AllQuestions.getChildCount(); i++) {
                    System.out.println("AllQuestions");
                    View temp0 = AllQuestions.getChildAt(i);
                    if(temp0 instanceof LinearLayout){
                    LinearLayout OneQuestion = (LinearLayout) AllQuestions.getChildAt(i);
                    for(int j=0;j<OneQuestion.getChildCount();j++){
                        System.out.println("OneQuestion");
                        View temp = OneQuestion.getChildAt(j);
                        System.out.println(temp.getClass());
                        if(temp instanceof android.support.v7.widget.CardView) {
                            System.out.println("CardView");
                            android.support.v7.widget.CardView cardView = (android.support.v7.widget.CardView) OneQuestion.getChildAt(j);
                            for (int m = 0; m < cardView.getChildCount(); m++) {
                                System.out.println("inCardView");
                                View temp2 = cardView.getChildAt(m);
                                JSONObject oneQueJson = new JSONObject();
                                if (temp2 instanceof LinearLayout) {
                                    LinearLayout inOneQuestion = (LinearLayout) cardView.getChildAt(m);
                                    for (int n = 0; n < inOneQuestion.getChildCount(); n++) {
                                        System.out.println("RadioGroup");
                                        View temp3 = inOneQuestion.getChildAt(n);
                                        if (temp3 instanceof EditText) {
                                            EditText QuestionHead = (EditText) inOneQuestion.getChildAt(n);
                                            try {
                                                oneQueJson.put("description", QuestionHead.getText());
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                        if (temp3 instanceof RadioGroup) {
                                            System.out.println("temp3");
                                            String options = "";
                                            RadioGroup radioGroup = (RadioGroup) inOneQuestion.getChildAt(n);
                                            EditText editA = (EditText) radioGroup.getChildAt(1);
                                            EditText editB = (EditText) radioGroup.getChildAt(3);
                                            EditText editC = (EditText) radioGroup.getChildAt(5);
                                            EditText editD = (EditText) radioGroup.getChildAt(7);
                                            options += "A:" + editA.getText() + ",B:" + editB.getText() + ",C:" + editC.getText() + ",D:" + editD.getText();
                                            try {
                                                oneQueJson.put("options", options);
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                            for (int o = 0; o < radioGroup.getChildCount(); o++) {
                                                View temp4 = radioGroup.getChildAt(o);
                                                if (temp4 instanceof RadioButton) {
                                                    System.out.println("temp4");
                                                    RadioButton radioButton = (RadioButton) radioGroup.getChildAt(o);
                                                    System.out.println("judge below");
                                                    if (radioButton.isChecked()) {
                                                        String answer = "";
                                                        System.out.println("This is selected");
                                                        if (o == 0)
                                                            answer += "A";
                                                        else if (o == 2)
                                                            answer += "B";
                                                        else if (o == 4)
                                                            answer += "C";
                                                        else if (o == 6)
                                                            answer += "D";
                                                        try {
                                                            oneQueJson.put("answer", answer);
                                                            jsonArray.put(oneQueJson);
                                                        } catch (JSONException e) {
                                                            e.printStackTrace();
                                                        }
//                                                        EditText edit = (EditText) radioGroup.getChildAt(o+1);
//                                                        System.out.println("EditText:");
//                                                        System.out.println(edit.getText());
                                                        System.out.println(jsonArray);
                                                        System.out.println("------------------------------");
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
//                            RadioGroup mRadioGroup = (RadioGroup) OneQuestion.getChildAt(j);
//                            RadioButton radioButton = (RadioButton)findViewById(mRadioGroup.getCheckedRadioButtonId());
//                            System.out.println(mRadioGroup.getCheckedRadioButtonId());
                        }
                    }
                }
                try {
//                    DataJson.put("paperName", PaperName.getText());
//                    DataJson.put("content", jsonArray);
                    CoreJson.put("data",jsonArray.getJSONObject(0));
                    CoreJson.put("type", 2);
                    CoreJson.put("topic",questionName.getText());
                    sendQuestion(CoreJson);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                System.out.println("CoreJson is::::::::::::::::::::::::::::::::::::::::::;");
                System.out.println(CoreJson);
            }
        });
    }

    private void initCtrl()
    {
        llContentView = findViewById(R.id.questionVerticalAll);

        final LayoutInflater inflater = LayoutInflater.from(this);
        // 获取需要被添加控件的布局
        final LinearLayout lin = (LinearLayout) findViewById(R.id.questionVerticalAll);
        // 获取需要添加的布局
        LinearLayout layout = (LinearLayout) inflater.inflate(
                R.layout.student_questionreleasecard, null).findViewById(R.id.ReleaseOneQuestion);
        // 将布局加入到当前布局中
        lin.addView(layout);
    }
}
