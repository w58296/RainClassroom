package com.bugcoder.sc.student.course;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bugcoder.sc.student.Student_GoodView;
import com.bugcoder.sc.student.R;
import com.bugcoder.sc.student.Student_User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Student_MyTalk extends AppCompatActivity {

    LinearLayout layout ;
    SwipeRefreshLayout mSwipeLayout;
    private String stuId;
    private String courseName;
    private String date;
    private String name;
    Student_User user = new Student_User();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.student_activity_talk);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        layout = findViewById(R.id.center1);

        Student_Talk talk = (Student_Talk) getIntent().getSerializableExtra("talk_data");
        stuId=talk.getStuId();
        date=talk.getDate();
        courseName=talk.getStuName();
        name=talk.getName();
        load();
        final  TextView talk_name=findViewById(R.id.talk_name);
        final  TextView talk_question=findViewById(R.id.talk_question);
        final  TextView talk_date=findViewById(R.id.talk_date);
        final  TextView good_a=findViewById(R.id.good_a);
        final Student_GoodView mGoodView = new Student_GoodView(this);
        final  ImageView good=findViewById(R.id.good);
        final  ImageView book=findViewById(R.id.bookmark);
        final  ScrollView scrollView=findViewById(R.id.scrollView);
        scrollView.fullScroll(ScrollView.FOCUS_DOWN);
        mSwipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_ly);
//设置在listview上下拉刷新的监听
        mSwipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //这里可以做一下下拉刷新的操作
                //例如下面代码，在方法中发送一个handler模拟延时操作
                System.out.println("13454532532");
                load();
                Toast.makeText(Student_MyTalk.this,"刷新成功", Toast.LENGTH_SHORT).show();

                mSwipeLayout.setRefreshing(false);


            }
        });

        talk_name.setText(talk.getStuId());
        talk_question.setText(talk.getQuestion());
        talk_date.setText(talk.getDate());
        good_a.setText(talk.getGood());
        good.setOnClickListener(new View.OnClickListener() {

            boolean isChanged=false;
            @Override
            public void onClick(View view) {

                if(!isChanged ){
                    ((ImageView) view).setImageResource(R.drawable.student_good_checked);
                    mGoodView.setText("+1");
                    int a=Integer.parseInt(good_a.getText().toString())+1;
                    good_a.setText(Integer.toString(a));
                    mGoodView.show(view);
                }else {
                    ((ImageView) view).setImageResource(R.drawable.student_good);
                    int a=Integer.parseInt(good_a.getText().toString())-1;
                    good_a.setText(Integer.toString(a));
                }
                isChanged = !isChanged;
            }
        });
        book.setOnClickListener(new View.OnClickListener() {
            boolean scisChanged=false;
            @Override
            public void onClick(View view) {
                if(!scisChanged ){
                    ((ImageView) view).setImageResource(R.drawable.student_bookmark_checked);
                    mGoodView.setTextInfo("标记成功", Color.parseColor("#ff941A"), 12);
                    mGoodView.show(view);
                }else {
                    ((ImageView) view).setImageResource(R.drawable.student_bookmark);
                }
                scisChanged = !scisChanged;
            }

        });




        //循环加载
//        layout.addView(createCardView("1","1","1"));
        //进行讨论
        Button talk_send=findViewById(R.id.talk_send);
//        ScrollView scrollView=findViewById(R.id.scrollView);
//        scrollView.fullScroll(ScrollView.FOCUS_DOWN);
        talk_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText et =findViewById(R.id.send_message);
                String q= et.getText().toString();//获取文本
                Date date = new Date(System.currentTimeMillis());  //系统当前时间
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
                String d= dateFormat.format(date);//获取时间
                layout.addView(createCardView(stuId,q,d));
                scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                et.setText("");

                //加载发送数据
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("stuId",name);
                    jsonObject.put("courseName",courseName);
                    jsonObject.put("date",talk_date.getText().toString());
                    jsonObject.put("tstuId",stuId);
                    jsonObject.put("assess",q);
                    jsonObject.put("tdate",d);
                    jsonObject.put("signal","TsavaQuestion");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                System.out.println(jsonObject);

                OkHttpClient okHttpClient = new OkHttpClient();
                RequestBody requestBody = FormBody.create(MediaType.parse("application/json; charset=utf-8"), String.valueOf(jsonObject));
                Request request = new Request.Builder()
                        .url("http://192.168.43.157:8080/stu/dis/sttdiscuss")//请求的url
                        .post(requestBody)
                        .build();

                Call call = okHttpClient.newCall(request);
                System.out.println("请求成功");
                //返回
                call.enqueue(new okhttp3.Callback(){
                    @Override
                    public void onFailure(Call call, IOException e) {
                        System.out.println("数据获取失败:"+e.toString());
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        Message msg = myHandler.obtainMessage();
                        msg.what = 1;
                        msg.obj = response.body().string();
                        myHandler.sendMessage(msg);
                        System.out.println("Handle发送："+ msg.obj);
                    }
                });
            }
        });
    }
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
    Handler myHandler = new Handler() {
        public void handleMessage(Message msg) {
            System.out.println("获得handle事件");
            System.out.println("Handle处理："+ msg.obj);

            try {
                if(msg.what==2){
                    parseJsonWithJsonObject((String) msg.obj);
                }
                else{

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            super.handleMessage(msg);


        }
    };
    private void parseJsonWithJsonObject(String responseData) throws IOException {

        System.out.println("789");
        //String responseData=response.body().string();
        System.out.println(responseData);
        layout.removeAllViews();
        try {
//            layout.addView(createCardView("1","2","3"));
//            layout.addView(createCardView("1","2","3"));
//            layout.addView(createCardView("1","2","3"));
            JSONObject jsonObject = new JSONObject(responseData);
            JSONArray jsonArray = jsonObject.getJSONArray("data");
            System.out.println("array:" + jsonArray.length());
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                String stuId=obj.getString("stuId");
                String assess=obj.getString("assess");
                String date=obj.getString("tdate");
                layout.addView(createCardView(stuId,assess,date));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }



    public CardView createCardView(final String name, final String question, final String data){
        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/Student_calibri.ttf");
        final Student_GoodView mGoodView = new Student_GoodView(this);


        //布局
        LinearLayout.LayoutParams lp_cardView = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        LinearLayout.LayoutParams lp_texteView = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT ,LinearLayout.LayoutParams.WRAP_CONTENT);
        LinearLayout.LayoutParams lp_texteView1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT ,LinearLayout.LayoutParams.WRAP_CONTENT);
        LinearLayout.LayoutParams lp_texteView2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT ,LinearLayout.LayoutParams.WRAP_CONTENT);
        LinearLayout.LayoutParams lp_texteView3 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT ,LinearLayout.LayoutParams.WRAP_CONTENT);
        LinearLayout.LayoutParams lp_texteView4 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT ,LinearLayout.LayoutParams.WRAP_CONTENT);
//        LinearLayout.LayoutParams lp_texteView5 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT ,LinearLayout.LayoutParams.WRAP_CONTENT);

        //视图属性
        final TextView textView=new TextView(this);

        textView.setText(name);

        lp_texteView.setMargins(0,20,0,20);
        textView.setLayoutParams(lp_texteView);
        textView.setBackground(getResources().getDrawable(R.drawable.student_blue_rounded_solid));
        textView.setPadding(12,8,12,8);
        textView.setTextColor(getResources().getColor(R.color.white));
        textView.setTextSize(20);
        textView.setTypeface(typeface);
//视图属性
        TextView textView1=new TextView(this);

        textView1.setText(question);
        lp_texteView1.setMargins(0,20,0,20);
        textView1.setLayoutParams(lp_texteView);
        textView1.setPadding(12,8,12,8);
        textView1.setTextColor(Color.BLACK);
        textView1.setTextSize(20);
        textView1.setTypeface(typeface);
//视图属性
        final TextView textView2=new TextView(this);
        lp_texteView2.setMargins(0,20,0,20);
        textView2.setText(data);
        textView2.setLayoutParams(lp_texteView2);
        textView2.setTextSize(16);
        textView2.setTypeface(typeface);

        final TextView textView3=new TextView(this);
        textView3.setText("1");
        textView3.setTextSize(16);
        textView3.setTypeface(typeface);

        ImageView imageView=new ImageView(this);
        imageView.setMaxWidth(1);
        imageView.setMaxHeight(1);
        lp_texteView3.setMargins(50,20,0,20);
        imageView.setLayoutParams(lp_texteView3);
        imageView.setImageDrawable(getResources().getDrawable(R.drawable.student_good));
        imageView.setOnClickListener(new View.OnClickListener() {

            boolean isChanged=false;
            @Override
            public void onClick(View view) {

                if(!isChanged ){
                    ((ImageView) view).setImageResource(R.drawable.student_good_checked);
                    mGoodView.setText("+1");
                    int a=Integer.parseInt(textView3.getText().toString())+1;
                    textView3.setText(Integer.toString(a));
                    mGoodView.show(view);
                }else {
                    ((ImageView) view).setImageResource(R.drawable.student_good);
                    int a=Integer.parseInt(textView3.getText().toString())-1;
                    textView3.setText(Integer.toString(a));
                }
                isChanged = !isChanged;
            }
        });


        ImageView imageView2=new ImageView(this);
        imageView2.setMaxWidth(1);
        imageView2.setMaxHeight(1);
        lp_texteView4.setMargins(50,20,0,20);
        imageView2.setLayoutParams(lp_texteView4);
        imageView2.setImageDrawable(getResources().getDrawable(R.drawable.student_bookmark));
        imageView2.setOnClickListener(new View.OnClickListener() {
            boolean scisChanged=false;
            @Override
            public void onClick(View view) {

                if(!scisChanged ){
                    ((ImageView) view).setImageResource(R.drawable.student_bookmark_checked);
                    mGoodView.setTextInfo("标记成功", Color.parseColor("#ff941A"), 12);
                    mGoodView.show(view);
                }else {
                    ((ImageView) view).setImageResource(R.drawable.student_bookmark);
                }
                scisChanged = !scisChanged;
            }
        });

        LinearLayout linearLayout=new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setLayoutParams(lp_cardView);
        linearLayout.addView(textView);
        linearLayout.addView(textView1);

        LinearLayout linearLayout1=new LinearLayout(this);
        linearLayout1.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout1.addView(textView2);
        linearLayout1.addView(imageView);
        linearLayout1.addView(textView3);
        linearLayout1.addView(imageView2);
        linearLayout.addView(linearLayout1);
//        android.support.v7.widget.CardView cardView = new android.support.v7.widget.CardView(this);
        final CardView cardView=new CardView(this);
        lp_cardView.setMargins(50,20,50,20);
        cardView.setLayoutParams(lp_cardView);
        cardView.setRadius(16);
        cardView.setElevation(4);
        cardView.setUseCompatPadding(true);
        cardView.setContentPadding(16,16,16,16);
        cardView.addView(linearLayout);
        return cardView;
    }
    void load(){

        //加载发送数据
        JSONObject jsonObject = new JSONObject();
        try {

            jsonObject.put("courseName",courseName);
            jsonObject.put("date",date);
            jsonObject.put("stuId",name);
            jsonObject.put("signal","loadQuestion");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        OkHttpClient okHttpClient = new OkHttpClient();
        String url = user.IP+"/stu/dis/enterDiss";
        RequestBody requestBody = FormBody.create(MediaType.parse("application/json; charset=utf-8"), String.valueOf(jsonObject));
        Request request = new Request.Builder()
                .url(url)//请求的url
                .post(requestBody)
                .build();

        Call call = okHttpClient.newCall(request);
        System.out.println("请求成功");
        //返回
        call.enqueue(new okhttp3.Callback(){
            @Override
            public void onFailure(Call call, IOException e) {
                System.out.println("数据获取失败:"+e.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Message msg = myHandler.obtainMessage();
                msg.what = 2;
                msg.obj = response.body().string();
                myHandler.sendMessage(msg);
                System.out.println("Handle发送："+ msg.obj);
            }
        });


    }

}