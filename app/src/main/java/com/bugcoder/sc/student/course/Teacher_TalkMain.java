package com.bugcoder.sc.student.course;

import android.content.Intent;
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

import com.bugcoder.sc.student.R;
import com.bugcoder.sc.student.Teacher_GoodView;
import com.bugcoder.sc.student.Teacher_User;

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

public class Teacher_TalkMain extends AppCompatActivity {

    private LinearLayout layout;

    private SwipeRefreshLayout mSwipeLayout;
    private String stuId;
    private String courseName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_talkmain);
        Intent temp = getIntent();
        stuId = temp.getStringExtra("stuId");
        courseName = temp.getStringExtra("courseName");
        System.out.println(courseName );


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        layout = findViewById(R.id.center);
        load();
        Button talk_send=findViewById(R.id.talk_send);
       final ScrollView scrollView=findViewById(R.id.scrollView);
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
                Toast.makeText(Teacher_TalkMain.this,"刷新成功", Toast.LENGTH_SHORT).show();

                mSwipeLayout.setRefreshing(false);


            }
        });


        talk_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EditText et =findViewById(R.id.send_message);
                String q= et.getText().toString();//获取文本
                Date date = new Date(System.currentTimeMillis());  //系统当前时间
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
                String d= dateFormat.format(date);//获取时间
                scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                layout.addView(createCardView(stuId,q,d,"0"));
                //加载发送数据
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("stuId",stuId);
                    jsonObject.put("courseName",courseName);
                    jsonObject.put("question",q);
                    jsonObject.put("date",d);
                    jsonObject.put("signal","savaQuestion");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
              System.out.println(jsonObject);
                OkHttpClient okHttpClient = new OkHttpClient();
                Teacher_User user = new Teacher_User();
                String url = user.IP+"/stu/dis/sendQuestion";
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
                        msg.what = 1;
                        msg.obj = response.body().string();
                        myHandler.sendMessage(msg);
                        System.out.println("Handle发送："+ msg.obj);
                    }
                });

                et.setText("");
            }
        });

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


   void  updataTotalgood(String courseName,String stuId,String date,String totalgood,String TstuId){
       //加载发送数据
       JSONObject jsonObject = new JSONObject();
       try {
           jsonObject.put("stuId",stuId);
           jsonObject.put("TstuId",TstuId);
           jsonObject.put("courseName",courseName);
           jsonObject.put("totalgood",totalgood);
           jsonObject.put("date",date);
           jsonObject.put("signal","update");
       } catch (JSONException e) {
           e.printStackTrace();
       }
       System.out.println(jsonObject);
       OkHttpClient okHttpClient = new OkHttpClient();
       Teacher_User user = new Teacher_User();
       String url = user.IP+"/stu/dis/sendQuestion";
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
               msg.what = 1;
               msg.obj = response.body().string();
               myHandler.sendMessage(msg);
               System.out.println("Handle发送："+ msg.obj);
           }
       });

   }


    private void parseJsonWithJsonObject(String responseData) throws IOException {

        System.out.println("789");
        //String responseData=response.body().string();
        System.out.println(responseData);
        layout.removeAllViews();
        try {
            JSONObject jsonObject = new JSONObject(responseData);
            JSONArray jsonArray = jsonObject.getJSONArray("data");
            System.out.println("array:" + jsonArray.length());

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                String stuId=obj.getString("stuId");
                String question=obj.getString("question");
                String date=obj.getString("date");
                String totalgood=obj.getString("totalgood");
                layout.addView(createCardView(stuId,question,date,totalgood));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    /**

     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp

     */


    public CardView createCardView(final String name, final String question, final String data ,final String totalgood){
        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/calibri.ttf");
        final Teacher_GoodView mGoodView = new Teacher_GoodView(this);

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
        textView.setBackground(getResources().getDrawable(R.drawable.blue_rounded_solid));
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
        textView3.setText(totalgood);
        textView3.setTextSize(16);
        textView3.setTypeface(typeface);

        ImageView imageView=new ImageView(this);
        imageView.setMaxWidth(1);
        imageView.setMaxHeight(1);
        lp_texteView3.setMargins(50,20,0,20);
        imageView.setLayoutParams(lp_texteView3);
        imageView.setImageDrawable(getResources().getDrawable(R.drawable.good));

        imageView.setOnClickListener(new View.OnClickListener() {
            boolean isChanged=false;
            @Override
            public void onClick(View view) {
                if(!isChanged ){
                    ((ImageView) view).setImageResource(R.drawable.good_checked);
                    mGoodView.setText("+1");
                    int a=Integer.parseInt(textView3.getText().toString())+1;
                    updataTotalgood(courseName,name,data,Integer.toString(a),stuId);
                    textView3.setText(Integer.toString(a));
                    mGoodView.show(view);
                }else {
                    ((ImageView) view).setImageResource(R.drawable.good);
                    int a=Integer.parseInt(textView3.getText().toString())-1;
                    updataTotalgood(courseName,name,data,Integer.toString(a),stuId);
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
        imageView2.setImageDrawable(getResources().getDrawable(R.drawable.bookmark));
        imageView2.setOnClickListener(new View.OnClickListener() {
            boolean scisChanged=false;
            @Override
            public void onClick(View view) {
                if(!scisChanged ){
                    ((ImageView) view).setImageResource(R.drawable.bookmark_checked);
                    mGoodView.setTextInfo("标记成功", Color.parseColor("#ff941A"), 12);
                    mGoodView.show(view);
                }else {
                    ((ImageView) view).setImageResource(R.drawable.bookmark);
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


        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                Teacher_Talk talk=new Teacher_Talk(stuId,courseName,question,data,textView3.getText().toString(),name);
                Intent intent = new Intent(getApplicationContext(), Teacher_MyTalk.class);
                intent.putExtra("talk_data",talk);
                startActivity(intent);
            }
        });
        return cardView;
    }

    void load(){
               //加载发送数据
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("stuId",stuId);
            jsonObject.put("courseName",courseName);
            jsonObject.put("signal","loadQuestion");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        System.out.println(jsonObject);
        OkHttpClient okHttpClient = new OkHttpClient();
        Teacher_User user = new Teacher_User();
        String url = user.IP+"/stu/dis/enterQues";
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

//    private void xc() {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                while(true) {
////                    int time = new Random().nextInt(300);
//                    load();
//                    try {
//                        Thread.sleep(1000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        }).start();
//    }
}
