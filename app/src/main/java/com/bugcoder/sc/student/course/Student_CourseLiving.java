package com.bugcoder.sc.student.course;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.bugcoder.sc.student.R;
import com.bugcoder.sc.student.Student_User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.UUID;
import java.util.Vector;

import master.flame.danmaku.controller.DrawHandler;
import master.flame.danmaku.danmaku.model.BaseDanmaku;
import master.flame.danmaku.danmaku.model.DanmakuTimer;
import master.flame.danmaku.danmaku.model.IDanmakus;
import master.flame.danmaku.danmaku.model.android.DanmakuContext;
import master.flame.danmaku.danmaku.model.android.Danmakus;
import master.flame.danmaku.danmaku.parser.BaseDanmakuParser;
import master.flame.danmaku.ui.widget.DanmakuView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class Student_CourseLiving extends AppCompatActivity  implements View.OnClickListener{
    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;
    //声明定位回调监听器
    public AMapLocationListener mLocationListener;
    //声明AMapLocationClientOption对象
    public AMapLocationClientOption mLocationOption = null;


    private boolean run = false;
    private String tempstr = "1";
    private Handler handler;
    private final Handler handler2 = new Handler();
    Student_MyDialog mMyDialog;
    android.support.v7.widget.CardView cardView;
    LinearLayout ll_course_list;
    LinearLayout ll_content;
    Button open;
    Button close;
    Button bt_to_talk;
    Button get_slide;
    Button releaseSlide;
    Button releaseQue;
    Button releasePaper;
    Button releaseSign;
    private boolean showDanmaku;
    private DanmakuView danmakuView;
    private DanmakuContext danmakuContext;
    private BaseDanmakuParser parser = new BaseDanmakuParser() {
        @Override
        protected IDanmakus parse() {
            return new Danmakus();
        }
    };
    LinearLayout operationLayout ;
    Vector v = new Vector();
    private String stuId;
    private String courseName;
    private String sign;
    double latitude;
    double longtitude;
    int count ;
    Student_User user= new Student_User();
    private String path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.student_activity_course_living);

        if (Build.VERSION.SDK_INT >= 23) {
            int REQUEST_CODE_CONTACT = 101;
            String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
            //验证是否许可权限
            for (String str : permissions) {
                if (this.checkSelfPermission(str) != PackageManager.PERMISSION_GRANTED) {
                    //申请权限
                    this.requestPermissions(permissions, REQUEST_CODE_CONTACT);
                }
            }
        }

        Intent temp = getIntent();
        stuId = temp.getStringExtra("stuId");
        courseName = temp.getStringExtra("courseName");
        count = 0;
        initUI();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        System.out.println(sHA1(this));
        mLocationListener = new AMapLocationListener() {
            @Override
            public void onLocationChanged(AMapLocation aMapLocation) {
//                System.out.println("amaplocation KKKKKKKKKKKK");
                if (aMapLocation != null) {
//                    System.out.println("amaplocation.getErrorCode() KKKKKKKKKKKK");
                    if (aMapLocation.getErrorCode() == 0) {
                        //可在其中解析amapLocation获取相应内容。
                        int type = aMapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见定位类型表
                        latitude = aMapLocation.getLatitude();//获取纬度
                        longtitude = aMapLocation.getLongitude();//获取经度
                        float accuracy = aMapLocation.getAccuracy();//获取精度信息
                        String floor = aMapLocation.getFloor();//获取当前室内定位的楼层
                        int gpsAccuracyStatus = aMapLocation.getGpsAccuracyStatus();//获取GPS的当前状态
                        //获取定位时间
                        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        Date date = new Date(aMapLocation.getTime());
                        df.format(date);

                        System.out.println("Type:"+type);
                        System.out.println("Latitude:"+latitude);
                        System.out.println("Longtitude:"+longtitude);
                        System.out.println("Accuracy:"+accuracy);
                        System.out.println("Floor:"+floor);
                        System.out.println("GpsAccuracyStatus:"+gpsAccuracyStatus);
                        System.out.println("Date:"+date);
                        mLocationClient.stopLocation();//停止定位后，本地定位服务并不会被销毁
                    }else {
                        //定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
                        Log.e("AmapError","location Error, ErrCode:"
                                + aMapLocation.getErrorCode() + ", errInfo:"
                                + aMapLocation.getErrorInfo());
                    }
                }
            }
        };
        //初始化定位
        mLocationClient = new AMapLocationClient(getApplicationContext());
        //设置定位回调监听
        mLocationClient.setLocationListener(mLocationListener);


        //初始化AMapLocationClientOption对象
        mLocationOption = new AMapLocationClientOption();
        AMapLocationClientOption option = new AMapLocationClientOption();
        /**
         * 设置定位场景，目前支持三种场景（签到、出行、运动，默认无场景）
         */
        option.setLocationPurpose(AMapLocationClientOption.AMapLocationPurpose.SignIn);
        if(null != mLocationClient){
            mLocationClient.setLocationOption(option);
            //设置场景模式后最好调用一次stop，再调用start以保证场景模式生效
            mLocationClient.stopLocation();
            mLocationClient.startLocation();
        }
        //设置定位模式为AMapLocationMode.Hight_Accuracy，高精度模式。
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //获取一次定位结果：
        //该方法默认为false。
        mLocationOption.setOnceLocation(true);

        //获取最近3s内精度最高的一次定位结果：
        //设置setOnceLocationLatest(boolean b)接口为true，启动定位时SDK会返回最近3s内精度最高的一次定位结果。如果设置其为true，setOnceLocation(boolean b)接口也会被设置为true，反之不会，默认为false。
        mLocationOption.setOnceLocationLatest(true);
        //设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setNeedAddress(true);
        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
        //启动定位
        mLocationClient.startLocation();

        System.out.println("SHA1"+sHA1(this));

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                //refreshUI((String)msg.obj);
                switch(msg.what){
                    //json字符串
                    case 1:
                        try {
                            JSONDM((String)msg.obj);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        break;
                    case 2:
                        try {
                            JSONSign((String)msg.obj);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        break;
                    case 3:
                        try {
                            JSONUI((String)msg.obj);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        break;
                    case 4:
                        try {
                            JSONSlide((String)msg.obj);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        break;
                }
            }
        };

        //一秒刷新一次
        run = true;
        handler.postDelayed(task, 1000);

//        bt_to_talk.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                Intent intent = new Intent(getApplicationContext(),Student_TalkMain.class);
//                startActivity(intent);
//            }
//        });
        danmakuView = (DanmakuView) findViewById(R.id.danmaku_view);
        danmakuView.enableDanmakuDrawingCache(true);
        danmakuView.setCallback(new DrawHandler.Callback() {
            @Override
            public void prepared() {
                showDanmaku = true;
                danmakuView.start();
//                generateSomeDanmaku();
            }

            @Override
            public void updateTimer(DanmakuTimer timer) {

            }

            @Override
            public void danmakuShown(BaseDanmaku danmaku) {

            }

            @Override
            public void drawingFinished() {

            }
        });
        danmakuContext = DanmakuContext.create();
        danmakuView.prepare(parser, danmakuContext);

        operationLayout = findViewById(R.id.operation_layout);
        final Button send = (Button) findViewById(R.id.send);
        final EditText editText = (EditText) findViewById(R.id.edit_text);
//        danmakuView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (operationLayout.getVisibility() == View.GONE) {
//                    operationLayout.setVisibility(View.VISIBLE);
//                } else {
//                    operationLayout.setVisibility(View.GONE);
//                }
//            }
//        });
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String content = editText.getText().toString();
                if (!TextUtils.isEmpty(content)) {
                    addDanmaku(content, true);
                    JSONObject Json = new JSONObject();
                    try {
                        Json.put("courseName",courseName);
                        Json.put("content",content);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    testaddDM(Json);
                    editText.setText("");
                }
            }
        });
        getWindow().getDecorView().setOnSystemUiVisibilityChangeListener (new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                if (visibility == View.SYSTEM_UI_FLAG_VISIBLE) {
                    onWindowFocusChanged(true);
                }
            }
        });



        //Student_Schedule schedule = (Student_Schedule) getIntent().getSerializableExtra("schedule");
        //System.out.println(schedule.courseName);
        //sign();
        /*Student_HttpUtil.sendRequestWithOkhttp("http://192.168.43.51:8000/course/findAllStuCourse/17301069", new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                System.out.println("数据获取失败");
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
        addItem(2);*/
    }


    private final Runnable task = new Runnable() {
        @Override
        public void run() {
            // TODO Auto-generated method stub
            if (run) {
                JSONObject testJson = new JSONObject();
                try {
                    testJson.put("courseName",courseName);
                    testRefreshDM(testJson);
                    testRefreshUI(testJson);

                    //js.put("sg",stuId);
//                    testRefreshSign(js);

                    handler2.postDelayed(this, 1000);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    public void testRefreshDM(JSONObject CoreJson){
        OkHttpClient mOkHttpClient = new OkHttpClient();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        String url = user.IP+"/loadDM";
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
                System.out.println(json);
                Message msg = handler.obtainMessage();
                msg.what = 1;
                msg.obj = json;
                handler.sendMessage(msg);
            }
        });
    }
    public void testRefreshSign(JSONObject CoreJson){
        OkHttpClient mOkHttpClient = new OkHttpClient();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        String url = user.IP+"/stu/course/teacher/sign";
        Request request = new Request.Builder()
                .url(url)
                .post(RequestBody.create(JSON, CoreJson.toString()))
                .build();

        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

//                System.out.println("failure");
                System.out.println("return failed::::::::::::::::::::::::::::::::");
                System.out.println(e.toString());
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                System.out.println("success");
                System.out.println("sign student list::::::::::::::::::::::::::::::::::::::::");
                String json = response.body().string();
                System.out.println(json);

                Message msg = handler.obtainMessage();
                msg.what = 2;
                msg.obj = json;
                handler.sendMessage(msg);
            }
        });
    }
    public void testRefreshUI(JSONObject CoreJson){
        OkHttpClient mOkHttpClient = new OkHttpClient();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        String url = user.IP+"/3";
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
                System.out.println(json);

                Message msg = handler.obtainMessage();
                msg.what = 3;
                msg.obj = json;
                handler.sendMessage(msg);
            }
        });
    }
    public void testRefreshSlide(JSONObject CoreJson){
        OkHttpClient mOkHttpClient = new OkHttpClient();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        String url = user.IP+"/4";
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
                System.out.println(json);

                Message msg = handler.obtainMessage();
                msg.what = 4;
                msg.obj = json;
                handler.sendMessage(msg);
            }
        });
    }
    public void testaddDM(JSONObject json){
        OkHttpClient mOkHttpClient = new OkHttpClient();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        String url = user.IP+"/DM";
        Request request = new Request.Builder()
                .url(url)
                .post(RequestBody.create(JSON, json.toString()))
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
                System.out.println(json);

                Message msg = handler.obtainMessage();
                msg.what =5;
                msg.obj = json;
                handler.sendMessage(msg);
            }
        });
    }

    /**
     * 向弹幕View中添加一条弹幕
     * @param content
     *          弹幕的具体内容
     * @param  withBorder
     *          弹幕是否有边框
     */
    private void addDanmaku(String content, boolean withBorder) {
        BaseDanmaku danmaku = danmakuContext.mDanmakuFactory.createDanmaku(BaseDanmaku.TYPE_SCROLL_RL);
        danmaku.text = content;
        danmaku.padding = 5;
        danmaku.textSize = sp2px(20);
        int rand = new Random().nextInt(3);
        if(rand==0 ){
            danmaku.textColor = Color.RED;
        }else if (rand ==1 ){
            danmaku.textColor = Color.BLUE;
        }
        else{
            danmaku.textColor = Color.GREEN;
        }
        danmaku.setTime(danmakuView.getCurrentTime());
//        if (withBorder) {
//            danmaku.borderColor = Color.GREEN;
//        }
        danmaku.borderColor = Color.GREEN;
        danmakuView.addDanmaku(danmaku);
    }

    /**
     * 随机生成一些弹幕内容以供测试
     */
    private void generateSomeDanmaku() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(showDanmaku) {
//                    int time = new Random().nextInt(300);
//                    String content = "" + time + time;
//                    addDanmaku(content, false);
//                    try {
//                        Thread.sleep(time);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }

                }
            }
        }).start();
    }

    /**
     * sp转px的方法。
     */
    public int sp2px(float spValue) {
        final float fontScale = getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (danmakuView != null && danmakuView.isPrepared()) {
            danmakuView.pause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (danmakuView != null && danmakuView.isPrepared() && danmakuView.isPaused()) {
            danmakuView.resume();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        showDanmaku = false;
        if (danmakuView != null) {
            danmakuView.release();
            danmakuView = null;
        }
    }

    /*private void parseJsonWithJsonObject(String responseData) throws IOException {

        System.out.println("开始解析json"+responseData);

        try {
            JSONObject jsonObject = new JSONObject(responseData);
            JSONArray jsonArray = jsonObject.getJSONArray("data");
            System.out.println("array:" + jsonArray.length());
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                String major = obj.getString("major");
                String courseName = obj.getString("course_name");
                String teacherName = obj.getString("teacher_name");
                int taskNum = obj.getInt("task_num");
                int myProgress = obj.getInt("my_progress");
                System.out.println("json原长度: " + v.size());
                v.addElement(new Student_Course(major, courseName, teacherName, taskNum, myProgress));
                System.out.println("json现长度: " + v.size());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }*/

    private void JSONDM(String responseData) throws IOException {

        System.out.println("开始解析json"+responseData);

        try {
            JSONObject obj= new JSONObject(responseData);


                if(obj.getString("check").equals("true")){
                    String content = obj.getString("content");
                    if(showDanmaku){
                    addDanmaku(content, false);
                    }
                }else {

                }
    } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private void JSONSign(String responseData) throws IOException {

        ListView signlisttext = (ListView)findViewById(R.id.listView2);
        String show = "已签到学生: \n";
        try {
            JSONObject tempsign = new JSONObject(responseData);
            JSONArray allsign= tempsign.getJSONArray("list");
            for(int i=0;i<allsign.length();i++){
                show += allsign.getString(i);
                if(i!=allsign.length()-1){
                    show+=" ";
                }
            }
            String data[] = show.split(" ");
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,data);
            signlisttext.setAdapter(adapter);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    private void JSONUI(String responseData) throws IOException {

        System.out.println("开始解析json"+responseData);

        try {
            JSONObject jsonObject = new JSONObject(responseData);
            JSONArray jsonArray = jsonObject.getJSONArray("data");
            System.out.println("array:" + jsonArray.length());
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                int type = obj.getInt("type");
                String topic = obj.getString("topic");
                String content = obj.getString("content");
                addItem(type,topic,content);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private void JSONSlide(String responseData) throws IOException {

        System.out.println("开始解析json"+responseData);

        try {
            JSONObject jsonObject = new JSONObject(responseData);
            JSONArray jsonArray = jsonObject.getJSONArray("data");
            System.out.println("array:" + jsonArray.length());
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);

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


    //签到界面
    public void sign(int mcount) {

        if(mcount==0){
        View view = getLayoutInflater().inflate(R.layout.student_dialog_sign, null);
        mMyDialog = new Student_MyDialog(this, 0, 0, view, R.style.MyDialog);
        Button btn_sign = mMyDialog.findViewById(R.id.btn_sign);
        btn_sign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                JSONObject js = new JSONObject();
                try {
                    System.out.println("latitude::::::::::::::::::::::::::::::::::");
                    System.out.println(latitude);
                    js.put("x",latitude);
                    js.put("y",longtitude);
                    js.put("stu_id",stuId);
                    js.put("check","true");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                //js.put("sg",stuId);
                testRefreshSign(js);
                System.out.println("签到成功");
                mMyDialog.dismiss();
            }
        });
            mMyDialog.setCancelable(true);
            mMyDialog.show();
            count++;
        }
    }

    public void initUI() {
        ll_course_list = findViewById(R.id.course_list);
        bt_to_talk=findViewById(R.id.bt_to_talk);
        open = findViewById(R.id.bt_open_talk);
        close = findViewById(R.id.bt_close_talk);
        releaseSlide = findViewById(R.id.bt_get_slide);
        releasePaper = findViewById(R.id.bt_release_paper);
        releaseQue = findViewById(R.id.bt_release_que);
        releaseSign = findViewById(R.id.bt_release_sign);
        bt_to_talk.setOnClickListener(this);
        open.setOnClickListener(this);
        close.setOnClickListener(this);
        releaseSlide.setOnClickListener(this);
        releasePaper.setOnClickListener(this);
        releaseQue.setOnClickListener(this);
        releaseSign.setOnClickListener(this);
//        get_slide.setOnClickListener(this);
    }

    @Override
    public void onClick(View view){
        if (view.getId() == R.id.bt_to_talk){
            System.out.println("进入讨论区");
            Intent intent = new Intent(getApplicationContext(), Student_TalkMain.class);
            intent.putExtra("stuId",stuId);
            intent.putExtra("courseName",courseName);
            startActivity(intent);
        }else if(view.getId() == R.id.bt_open_talk){
            System.out.println("开启弹幕");
            operationLayout.setVisibility(View.VISIBLE);
            danmakuView.setVisibility(View.VISIBLE);
        }else if(view.getId() == R.id.bt_close_talk){
            System.out.println("关闭弹幕");
            operationLayout.setVisibility(View.INVISIBLE);
            danmakuView.setVisibility(View.INVISIBLE);
        } else if(view.getId() == R.id.bt_get_slide){
            System.out.println("发布PPT");
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            //intent.setType(“image/*”);//选择图片
            //intent.setType(“audio/*”); //选择音频
            //intent.setType(“video/*”); //选择视频 （mp4 3gp 是android支持的视频格式）
            //intent.setType(“video/*;image/*”);//同时选择视频和图片
            intent.setType("*/*");//无类型限制
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            startActivityForResult(intent, 1);

            JSONObject testJson = new JSONObject();
            try {
                testJson.put("courseName",courseName);
//                testRefreshSlide(testJson);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else if(view.getId() == R.id.bt_release_que){
            Intent intent = new Intent(getApplicationContext(), Student_TestQuestionRelease.class);
            intent.putExtra("stuId",stuId);
            intent.putExtra("courseName",courseName);
            startActivity(intent);

        }else if(view.getId() == R.id.bt_release_paper){
            Intent intent = new Intent(getApplicationContext(), Student_TestPaperRelease.class);
            intent.putExtra("stuId",stuId);
            intent.putExtra("courseName",courseName);
            startActivity(intent);
        }else if(view.getId() == R.id.bt_release_sign){
            JSONObject js = new JSONObject();
            try {
                js.put("x",latitude);
                js.put("y",longtitude);
                js.put("stu_id",stuId);
                js.put("check","true");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            testRefreshSign(js);
        }
    }

    public void addItem(int type,String topic,String content) {
        cardView = new android.support.v7.widget.CardView(this);
        LinearLayout.LayoutParams lp_cardView = new LinearLayout.LayoutParams(1000, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp_cardView.setMargins(0, 0, 0, 20);
        lp_cardView.gravity = Gravity.CENTER;
        cardView.setLayoutParams(lp_cardView);
        cardView.setRadius(8);
        cardView.setElevation(16);
        cardView.setUseCompatPadding(true);
        cardView.setContentPadding(16, 16, 16, 16);
        ll_course_list.addView(cardView);

        ll_content = new LinearLayout(this);
        LinearLayout.LayoutParams lp_ll_content = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        //lp_ll_content.gravity = Gravity.CENTER;
        lp_ll_content.setMargins(0, 40, 0, 40);
        ll_content.setGravity(Gravity.CENTER);
        ll_content.setLayoutParams(lp_ll_content);
        ll_content.setOrientation(LinearLayout.HORIZONTAL);
        cardView.addView(ll_content);

        ImageView im = new ImageView(this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.weight = 30;
        layoutParams.gravity = Gravity.CENTER;
        im.setLayoutParams(layoutParams);
        ll_content.addView(im);

        LinearLayout linearLayout = new LinearLayout(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.weight = 60;
        lp.gravity = Gravity.CENTER;
        linearLayout.setLayoutParams(lp);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        ll_content.addView(linearLayout);

        TextView tv_1 = new TextView(this);
        LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        tv_1.setLayoutParams(lp1);
        tv_1.setTextSize(20);
        tv_1.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        tv_1.setMaxLines(1);
        tv_1.setMaxEms(10);
        tv_1.setEllipsize(TextUtils.TruncateAt.valueOf("END"));
        linearLayout.addView(tv_1);

        TextView tv_2 = new TextView(this);
        LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp2.setMargins(0, 10, 0, 0);
        tv_2.setLayoutParams(lp2);
        tv_2.setTextSize(16);
        tv_2.setMaxLines(1);
        tv_2.setMaxEms(10);
        tv_2.setEllipsize(TextUtils.TruncateAt.valueOf("END"));
        linearLayout.addView(tv_2);

        if (type == 1) {
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    Intent intent = new Intent(Student_CourseLiving.this, Student_TestQuestioinAnswering.class);
                    startActivity(intent);
                }
            });
            im.setImageResource(R.drawable.student_ic_exam_black_50dp);
        } else if (type == 2) {
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    Intent intent = new Intent(Student_CourseLiving.this, Student_TestPaperAnswering.class);
                    startActivity(intent);
                }
            });
            im.setImageResource(R.drawable.student_ic_exercise_black_60dp);
        } else if (type == 3) {
//            cardView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    // TODO Auto-generated method stub
//                    Intent intent = new Intent(Student_CourseLiving.this, Student_TestQuestioinAnswering.class);
//                    startActivity(intent);
//                }
//            });
            im.setImageResource(R.drawable.student_ic_exam_black_50dp);
        } else {
            System.out.println("Error");
        }

        tv_1.setText(topic);
        tv_2.setText(content);
    }

    public void addTest() {
    }

    public void addExercise() {

    }

    public void addSlide() {

    }
    public static String sHA1(Context context){
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), PackageManager.GET_SIGNATURES);
            byte[] cert = info.signatures[0].toByteArray();
            MessageDigest md = MessageDigest.getInstance("SHA1");
            byte[] publicKey = md.digest(cert);
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < publicKey.length; i++) {
                String appendString = Integer.toHexString(0xFF & publicKey[i])
                        .toUpperCase(Locale.US);
                if (appendString.length() == 1)
                    hexString.append("0");
                hexString.append(appendString);
                hexString.append(":");
            }
            String result = hexString.toString();
            return result.substring(0, result.length()-1);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void sendFile(String filePath){
        String fileName;
//        List<String> temp = new ArrayList<String>();
        System.out.println("filePath:::::::::::::::::::::");
        System.out.println(filePath);
        System.out.println("absolutePath:::::::::::::::::::::");
        System.out.println(Environment.getExternalStorageDirectory()
                .getAbsolutePath());
        String temp[] = filePath.split("/");
        File file = new File(filePath);
        System.out.println("file exists:::::::::::::::::::::::::::::");
        System.out.println(file.exists());
        fileName = temp[temp.length-1];
        System.out.println(fileName);

        OkHttpClient mOkHttpClient = new OkHttpClient();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        Student_User user = new Student_User();
        JSONObject tempnamejson = new JSONObject();
        try {
            tempnamejson.put("fileName",fileName);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String url = user.IP+"/stu/router";
        Request request2 = new Request.Builder()
                .url(url)
                .post(RequestBody.create(JSON, tempnamejson.toString()))
                .build();

        mOkHttpClient.newCall(request2).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                System.out.println("failure");
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                System.out.println("success");
            }
        });


        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", fileName,
                        RequestBody.create(MediaType.parse("multipart/form-data"), file))
                .build();

        Request request = new Request.Builder()
                .header("Authorization", "Client-ID " + UUID.randomUUID())
                .url(user.IP+"/multiUpload")
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                System.out.println("failure");
                System.out.println(e.toString());
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                System.out.println("success");
                String json = response.body().string();
                System.out.println(json);
            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            Uri uri = data.getData();
            if ("file".equalsIgnoreCase(uri.getScheme())) {//使用第三方应用打开
                path = uri.getPath();
//                tv.setText(path);
                Toast.makeText(this, path + "11111", Toast.LENGTH_SHORT).show();
                return;
            }
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {//4.4以后
                path = getPath(this, uri);
//                tv.setText(path);
                Toast.makeText(this, path, Toast.LENGTH_SHORT).show();
                sendFile(path);
            }
        }
    }

    /**
     * 专为Android4.4设计的从Uri获取文件绝对路径，以前的方法已不好使
     */
    @SuppressLint("NewApi")
    public String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]};

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public String getDataColumn(Context context, Uri uri, String selection,
                                String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

}
