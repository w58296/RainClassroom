package com.bugcoder.sc.student;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bugcoder.sc.student.course.Teacher_CourseLiving;
import com.bugcoder.sc.student.course.Teacher_Courses;
import com.bugcoder.sc.student.favourite.Teacher_Collection;
import com.bugcoder.sc.student.schedule.Teacher_Schedule;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Vector;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Teacher_Home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    LinearLayout profile;
    LinearLayout ll_daily_schedule;
    String stuId;
    String courseName;

    Vector v = new Vector();

    int count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Intent temp = getIntent();
        stuId = temp.getStringExtra("stuId");
        System.out.println("stuId::::::::::::::::::::::::::::::::::");
        System.out.println(stuId);

        initData();
        initUI();
        Teacher_Schedule s = new Teacher_Schedule("Android", "Zhang Di", "08:00 - 10:00", "YF205", "12 Jul 2019");
        //addSchedule(s);
//        Teacher_Schedule s1 = new Teacher_Schedule("asdfghjkl","Zhang Di","08:00 - 10:00","YF205","12 Jul 2019");
//        addSchedule(s1);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("stuId",stuId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        System.out.println("stuId Json:::::::::::::::::::::::::::::::;");
        System.out.println(jsonObject);

        OkHttpClient okHttpClient = new OkHttpClient();
        Teacher_User user = new Teacher_User();
        String url = user.IP+"/stu/course/search";
        RequestBody requestBody = FormBody.create(MediaType.parse("application/json; charset=utf-8"), String.valueOf(jsonObject));
        Request request = new Request.Builder()
                .url(url)//请求的url
                .post(requestBody)
                .build();

        Call call = okHttpClient.newCall(request);
        System.out.println("请求成功");
        call.enqueue(new okhttp3.Callback() {
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
    }

    Handler myHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    try {
                        System.out.println("获得handle事件");
                        System.out.println("Handle处理："+ msg.obj);
                        parseJsonWithJsonObject((String) msg.obj);
                        for (int i = 0; i < v.size(); i++) {
                            System.out.println("start: " + i);
                            addSchedule((Teacher_Schedule) v.elementAt(i));
                            System.out.println(i);
                        }
                    } catch (IOException e) {

                    }
                    break;
            }
            super.handleMessage(msg);
        }
    };

    private void parseJsonWithJsonObject(String responseData) throws IOException {

        System.out.println("开始解析json"+responseData);

        try {
            JSONObject jsonObject = new JSONObject(responseData);
            JSONArray jsonArray = jsonObject.getJSONArray("data");
            System.out.println("array:" + jsonArray.length());
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                String courseName = obj.getString("courseName");
                String teacherName = obj.getString("teacherName");
                String time = obj.getString("time");
                String room = obj.getString("room");
                String date = obj.getString("date");
                System.out.println("json原长度: " + v.size());
                v.addElement(new Teacher_Schedule(courseName, teacherName, time, room,date));
                System.out.println("json现长度: " + v.size());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    void initData() {
        count = 0;
    }

    void initUI() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        View header = navigationView.getHeaderView(0);
        profile = header.findViewById(R.id.profile);
        profile.setOnClickListener(this);

        ll_daily_schedule = findViewById(R.id.ll_daily_schedule);
    }

    void addSchedule(final Teacher_Schedule schedule) {
        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/calibri.ttf");
        //ll_daily_schedule.removeView(ll_daily_schedule);

        android.support.v7.widget.CardView cardView = new android.support.v7.widget.CardView(this);
        LinearLayout.LayoutParams lp_cardView = new LinearLayout.LayoutParams(800, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp_cardView.setMargins(0, 10, 0, 0);
        cardView.setLayoutParams(lp_cardView);
        cardView.setRadius(8);
        cardView.setElevation(16);
        cardView.setUseCompatPadding(true);
        cardView.setContentPadding(16, 16, 16, 16);
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(getApplicationContext(), Teacher_CourseLiving.class);
                intent.putExtra("schedule", schedule);
                intent.putExtra("stuId",stuId);
                //intent.putExtra("courseName",courseName);
                startActivity(intent);
            }
        });
        ll_daily_schedule.addView(cardView);

        LinearLayout ll_card = new LinearLayout(this);
        ll_card.setOrientation(LinearLayout.VERTICAL);
        cardView.addView(ll_card);

        TextView tv_course_name = new TextView(this);
        LinearLayout.LayoutParams lp_tv1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp_tv1.setMargins(0, 0, 0, 12);
        tv_course_name.setLayoutParams(lp_tv1);
        tv_course_name.setTypeface(typeface);
        tv_course_name.setText(schedule.getCourseName());
        tv_course_name.setTextColor(Color.parseColor("#c45248"));
        tv_course_name.setTextSize(18);
        ll_card.addView(tv_course_name);

        TextView tv_teacher_name = new TextView(this);
        LinearLayout.LayoutParams lp_tv2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp_tv2.setMargins(0, 0, 0, 16);
        tv_teacher_name.setLayoutParams(lp_tv2);
        tv_teacher_name.setTypeface(typeface);
        tv_teacher_name.setTextColor(Color.parseColor("#000000"));
        tv_teacher_name.setText(schedule.getTeacherName());
        //tv_teacher_name.setTextSize(14);
        ll_card.addView(tv_teacher_name);

        View v = new View(this);
        LinearLayout.LayoutParams lp_v = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 3);
        lp_v.setMargins(0, 6, 0, 20);
        v.setLayoutParams(lp_v);
        v.setBackgroundColor(Color.parseColor("#80D1D1D1"));
        ll_card.addView(v);

        LinearLayout ll_content = new LinearLayout(this);
        LinearLayout.LayoutParams lp_ll_content = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp_ll_content.weight = 100;
        ll_content.setLayoutParams(lp_ll_content);
        ll_content.setOrientation(LinearLayout.HORIZONTAL);
        ll_card.addView(ll_content);

        LinearLayout ll_content_1 = new LinearLayout(this);
        LinearLayout.LayoutParams lp_ll_content_1 = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp_ll_content_1.weight = 50;
        ll_content_1.setLayoutParams(lp_ll_content);
        ll_content_1.setOrientation(LinearLayout.VERTICAL);
        ll_content.addView(ll_content_1);

        LinearLayout ll_content_2 = new LinearLayout(this);
        LinearLayout.LayoutParams lp_ll_content_2 = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp_ll_content_2.weight = 50;
        ll_content_2.setLayoutParams(lp_ll_content);
        ll_content_2.setOrientation(LinearLayout.VERTICAL);
        ll_content.addView(ll_content_2);

        TextView tv_time = new TextView(this);
        LinearLayout.LayoutParams lp_tv3 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp_tv3.setMargins(0, 0, 0, 16);
        //lp_tv3.gravity = Gravity.CENTER;
        tv_time.setLayoutParams(lp_tv3);
        tv_time.setTypeface(typeface);
        tv_time.setText(schedule.getTime());
        tv_time.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.ic_access_time_red_24dp), null, null, null);
        tv_time.setCompoundDrawablePadding(8);
        //tv_teacher_name.setTextSize(14);
        ll_content_1.addView(tv_time);

        TextView tv_date = new TextView(this);
        LinearLayout.LayoutParams lp_tv4 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp_tv4.setMargins(0, 0, 0, 6);
        //lp_tv4.gravity = Gravity.CENTER;
        tv_date.setLayoutParams(lp_tv4);
        tv_date.setTypeface(typeface);
        tv_date.setText(schedule.getDate());
        tv_date.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.ic_event_available_red_16dp), null, null, null);
        tv_date.setCompoundDrawablePadding(8);
        //tv_teacher_name.setTextSize(14);
        ll_content_1.addView(tv_date);

        TextView tv_room = new TextView(this);
        LinearLayout.LayoutParams lp_tv5 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp_tv5.setMargins(0, 0, 0, 16);
        //lp_tv5.gravity = Gravity.CENTER;
        tv_room.setLayoutParams(lp_tv5);
        tv_room.setTypeface(typeface);
        tv_room.setText(schedule.getRoom());
        tv_room.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.ic_location_on_red_24dp), null, null, null);
        tv_room.setCompoundDrawablePadding(8);
        //tv_teacher_name.setTextSize(14);
        ll_content_2.addView(tv_room);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home_bar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Teacher_Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        /*if (id == R.id.action_notification) {
            Intent intent = new Intent(getApplicationContext(), Events.class);
            startActivity(intent);
        }*/

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_courses) {
            Intent intent = new Intent(getApplicationContext(), Teacher_Courses.class);
            intent.putExtra("stuId",stuId);
            startActivity(intent);
        } /*else if (id == R.id.nav_events) {
            Intent intent = new Intent(getApplicationContext(), Events.class);
            startActivity(intent);
        }*/ else if (id == R.id.nav_announcements) {
            Intent intent = new Intent(getApplicationContext(), Teacher_Announcements.class);
            intent.putExtra("stuId",stuId);
            startActivity(intent);
        } else if (id == R.id.nav_favourite) {
            Intent intent = new Intent(getApplicationContext(), Teacher_Collection.class);
            intent.putExtra("stuId",stuId);
            startActivity(intent);
        } else if (id == R.id.nav_meno) {
            //Intent intent = new Intent(getApplicationContext(), example.rico.calendar.activity.MainActivity.class);
            //startActivity(intent);
        } else if (id == R.id.nav_logout) {
            Intent intent = new Intent(getApplicationContext(), Teacher_LoginScreen.class);
            intent.putExtra("stuId",stuId);
            startActivity(intent);
            finish();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.profile) {
            Intent intent = new Intent(getApplicationContext(), Teacher_MyProfile.class);
            startActivity(intent);
        }
    }
}
