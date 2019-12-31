package com.bugcoder.sc.student.course;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.bugcoder.sc.student.R;
import com.bugcoder.sc.student.Teacher_User;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
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
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;



public class Teacher_CourseLiving extends AppCompatActivity  implements View.OnClickListener{
    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;
    //声明定位回调监听器
    public AMapLocationListener mLocationListener;
    //声明AMapLocationClientOption对象
    public AMapLocationClientOption mLocationOption = null;

    ImageView image;
    private boolean run = false;
    private String tempstr = "1";
    private Handler handler;
    private final Handler handler2 = new Handler();
    Teacher_MyDialog mMyDialog;
    android.support.v7.widget.CardView cardView;
    LinearLayout ll_course_list;
    LinearLayout ll_content;
    Button open;
    Button close;
    Button bt_to_talk;
    Button get_slide;
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
    String paper_question_topic="";
    Teacher_User user= new Teacher_User();
    int num=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_living);
        Intent temp = getIntent();
        stuId = temp.getStringExtra("stuId");
        courseName = temp.getStringExtra("courseName");
        count = 0;
        initUI();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mLocationListener = new AMapLocationListener() {
            @Override
            public void onLocationChanged(AMapLocation aMapLocation) {
                if (aMapLocation != null) {
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
//                Intent intent = new Intent(getApplicationContext(),Teacher_TalkMain.class);
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


    }


    private final Runnable task = new Runnable() {
        @Override
        public void run() {
            // TODO Auto-generated method stub
            if (run) {
                JSONObject testJson = new JSONObject();
                try {
                    testJson.put("courseName",courseName);
                    testJson.put("paper_question_id",paper_question_topic);
                    testRefreshDM(testJson);
                    testRefreshUI(testJson);
                    JSONObject js = new JSONObject();
                    js.put("x",latitude);
                    js.put("y",longtitude);
                    js.put("stu_id",stuId);
                    System.out.println("STUID:::::::::::::::::::::::::::::::::::::::::::::::::::::::"+stuId);
                    js.put("check","false");
                    //js.put("sg",stuId);
                    testRefreshSign(js);

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
        String url = user.IP+"/stu/course/student/sign";
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
                msg.what = 2;
                msg.obj = json;
                handler.sendMessage(msg);
            }
        });
    }
    public void testRefreshUI(JSONObject CoreJson){
        OkHttpClient mOkHttpClient = new OkHttpClient();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        String url = user.IP+"/stu/course/paperorquestion";
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
        String url = user.IP+"/getfile/number";
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
                int json = Integer.parseInt(response.body().string());
                System.out.println(json);
                num=json;

            }
        });
        for(int i=0;i<num;i++){
            OkHttpClient mOkHttpClient1 = new OkHttpClient();
            MediaType JSON1 = MediaType.parse("application/json; charset=utf-8");
            String url1 = user.IP+"/download/"+i;
            System.out.println(url1);
            Request request1 = new Request.Builder()
                    .url(url1)
                    .post(RequestBody.create(JSON1, CoreJson.toString()))
                    .build();

            mOkHttpClient1.newCall(request1).enqueue(new Callback() {
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


    private void JSONDM(String responseData) throws IOException {

        System.out.println("开始解析DMjson"+responseData);

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

        System.out.println("开始解析QDjson"+responseData);

        try {
            JSONObject jsonObject = new JSONObject(responseData);
                String check =  jsonObject.getString("sign");
            System.out.println(check );
            System.out.println(check.equals("true") );
                if(check.equals("true")){
                    System.out.println("改签到了");
                    sign(count);
        }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private void JSONUI(String responseData) throws IOException {

        System.out.println("1232343546564567576756765开始解析UIjson"+responseData);

        try {
            JSONObject jsonObject = new JSONObject(responseData);
            String check = jsonObject.getString("check");

            if(check.equals("true")){
                    paper_question_topic = jsonObject.getString("topic");
                    int type = jsonObject.getInt("type");
                    //String topic = obj.getString("topic");
                    //String content = obj.getString("content");
                    addItem(type,paper_question_topic);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private void JSONSlide(String responseData) throws IOException {

        System.out.println("开始解析图片："+responseData);
        image = new ImageView(this);
        image.setScaleType(ImageView.ScaleType.FIT_XY);
        image.setImageBitmap(base64ToBitmap(responseData));
        addItem(3,"");
    }
    //base64转为bitmap
    public static Bitmap base64ToBitmap(String base64Data) {
        byte[] bytes = Base64.decode(base64Data, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }



    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }


    //签到界面
    public void sign(int mcount) {

        if(mcount==0){
        View view = getLayoutInflater().inflate(R.layout.dialog_sign, null);
        mMyDialog = new Teacher_MyDialog(this, 0, 0, view, R.style.MyDialog);
        Button btn_sign = mMyDialog.findViewById(R.id.btn_sign);
        btn_sign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                JSONObject js = new JSONObject();
                try {
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
        get_slide = findViewById(R.id.bt_get_slide);
        bt_to_talk.setOnClickListener(this);
        open.setOnClickListener(this);
        close.setOnClickListener(this);
        get_slide.setOnClickListener(this);
    }

    @Override
    public void onClick(View view){
        if (view.getId() == R.id.bt_to_talk){
            System.out.println("进入讨论区");
            Intent intent = new Intent(getApplicationContext(), Teacher_TalkMain.class);
            intent.putExtra("stuId",stuId);
            intent.putExtra("courseName",courseName);
            startActivity(intent);
        }else if(view.getId() == R.id.bt_open_talk){
            System.out.println("开启弹幕");
            operationLayout.setVisibility(View.VISIBLE);
            danmakuView.setVisibility(View.VISIBLE);
        }else if(view.getId() == R.id.bt_close_talk){
            System.out.println("关闭弹幕");
            operationLayout.setVisibility(View.GONE);
            danmakuView.setVisibility(View.GONE);
        } else if(view.getId() == R.id.bt_get_slide){
            System.out.println("获取PPT");
            JSONObject testJson = new JSONObject();
            try {
                testJson.put("courseName",courseName);
                testRefreshSlide(testJson);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void addItem(int type,String topic) {
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
            tv_2.setText("请大家抓紧时间考试！");
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    Intent intent = new Intent(Teacher_CourseLiving.this, Teacher_TestPaperAnswering.class);
                    startActivity(intent);
                }
            });
            im.setImageResource(R.drawable.ic_exam_black_50dp);
        } else if (type == 2) {
            tv_2.setText("有一道题哦！");
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    Intent intent = new Intent(Teacher_CourseLiving.this, Teacher_TestQuestioinAnswering.class);
                    startActivity(intent);
                }
            });
            im.setImageResource(R.drawable.ic_exercise_black_60dp);
        } else if (type == 3) {
            cardView.addView(image);
            System.out.println("已经加载图片:"+num);
//            cardView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    // TODO Auto-generated method stub
//                    Intent intent = new Intent(Teacher_CourseLiving.this, Teacher_TestQuestioinAnswering.class);
//                    startActivity(intent);
//                }
//            });
            //im.setImageResource(R.drawable.ic_exam_black_50dp);
        } else {
            System.out.println("Error");
        }

        tv_1.setText(topic);
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
}
