package com.bugcoder.sc.student;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bugcoder.sc.student.course.Student_SignupScreen;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Student_LoginScreen extends AppCompatActivity implements View.OnClickListener {

    Button btn_student_signIn, btn_teacher_signIn;
    TextView tv_signUp;
    TextView tv_login_id;
    TextView tv_login_password;

    public static Student_User student_uesr = new Student_User();
    public static Teacher_User teacher_uesr = new Teacher_User();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.student_activity_login_screen);
        btn_student_signIn = findViewById(R.id.btn_sign_in);
        btn_teacher_signIn = findViewById(R.id.btn_sign_in2);
        tv_signUp = findViewById(R.id.tv_sign_up);
        tv_login_id = findViewById(R.id.tv_login_id);
        tv_login_password = findViewById(R.id.tv_login_password);
        btn_student_signIn.setOnClickListener(this);
        btn_teacher_signIn.setOnClickListener(this);
        tv_signUp.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_sign_in) {
            String id = tv_login_id.getText().toString();
            String password = tv_login_password.getText().toString();
            if (id.equals("admin")) {
                if (password.equals("admin")) {
                    student_uesr.setId("admin");
                    Intent intent = new Intent(getApplicationContext(), Teacher_Home.class);
                    startActivity(intent);
                    this.finish();
                } else if (password.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Password Wrong!", Toast.LENGTH_SHORT).show();
                }
            } else if (id.isEmpty() || password.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Please input ID or password!", Toast.LENGTH_SHORT).show();
            } else {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("Id", id);
                    jsonObject.put("password", password);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                OkHttpClient okHttpClient = new OkHttpClient();
                RequestBody requestBody = FormBody.create(MediaType.parse("application/json; charset=utf-8"), String.valueOf(jsonObject));
                Student_User user = new Student_User();
                String url = user.IP + "/login";
                Request request = new Request.Builder()
                        .url(url)//请求的url
                        .post(RequestBody.create(JSON, jsonObject.toString()))
                        .build();

                okhttp3.Call call = okHttpClient.newCall(request);
                System.out.println("请求成功:" + jsonObject.toString());
                call.enqueue(new okhttp3.Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        System.out.println("数据获取失败:" + e.toString());
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        Message msg = myHandler.obtainMessage();
                        msg.what = 1;
                        msg.obj = response.body().string();
                        myHandler.sendMessage(msg);
                        System.out.println("Handle发送：" + msg.obj);
                    }
                });
            }

        } else if (view.getId() == R.id.tv_sign_up) {
            Intent intent = new Intent(getApplicationContext(), Student_SignupScreen.class);
            startActivity(intent);
        }
        if (view.getId() == R.id.btn_sign_in2) {
            String id = tv_login_id.getText().toString();
            String password = tv_login_password.getText().toString();
            if (id.equals("admin")) {
                if (password.equals("admin")) {
                    teacher_uesr.setId("admin");
                    Intent intent = new Intent(getApplicationContext(), Student_Home.class);
                    startActivity(intent);
                    this.finish();
                } else if (password.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Password Wrong!", Toast.LENGTH_SHORT).show();
                }
            } else if (id.isEmpty() || password.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Please input ID or password!", Toast.LENGTH_SHORT).show();
            } else {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("Id", id);
                    jsonObject.put("password", password);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                OkHttpClient okHttpClient = new OkHttpClient();
                RequestBody requestBody = FormBody.create(MediaType.parse("application/json; charset=utf-8"), String.valueOf(jsonObject));
                Teacher_User user = new Teacher_User();
                String url = user.IP + "/login";
                Request request = new Request.Builder()
                        .url(url)//请求的url
                        .post(RequestBody.create(JSON, jsonObject.toString()))
                        .build();

                Call call = okHttpClient.newCall(request);
                System.out.println("请求成功:" + jsonObject.toString());
                call.enqueue(new okhttp3.Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        System.out.println("数据获取失败:" + e.toString());
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        Message msg = myHandler.obtainMessage();
                        msg.what = 1;
                        msg.obj = response.body().string();
                        myHandler.sendMessage(msg);
                        System.out.println("Handle发送：" + msg.obj);
                    }
                });
            }

        }
    }

    Handler myHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    try {
                        System.out.println("获得handle事件");
                        System.out.println("Handle处理：" + msg.obj);
                        parseJsonWithJsonObject((String) msg.obj);
                    } catch (IOException e) {

                    }
                    break;
            }
            super.handleMessage(msg);
        }
    };

    private void parseJsonWithJsonObject(String responseData) throws IOException {

        System.out.println("开始解析json" + responseData);

        try {
            JSONObject jsonObject = new JSONObject(responseData);
//            uesr.setId(jsonObject.getString("data"));
            String check = jsonObject.getString("check");
            if (check.equals("tea")) {
                Intent intent = new Intent(getApplicationContext(), Student_Home.class);
                intent.putExtra("stuId", tv_login_id.getText().toString());
                startActivity(intent);

                this.finish();
            } else if (check.equals("stu")) {
                Intent intent = new Intent(getApplicationContext(), Teacher_Home.class);
                intent.putExtra("stuId", tv_login_id.getText().toString());
                startActivity(intent);

                this.finish();
            } else {
                Toast.makeText(getApplicationContext(), "ID or Password is Wrong!", Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
