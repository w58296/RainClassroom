package com.bugcoder.sc.student;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.CountDownTimer;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.view.View;


/**
 * Created by odvl on 2016/12/5.
 */

public class Student_CountDownTimeTextView extends AppCompatTextView {
    private static final String SPLIT = "  :  ";
    private static final String BLANK = "  ";
    private static final int RADIUS = 4;
    static final long MS_IN_A_DAY = 1000 * 60 * 60 * 24;
    static final long MS_IN_AN_HOUR = 1000 * 60 * 60;
    static final long MS_IN_A_MINUTE = 1000 * 60;
    static final long MS_IN_A_SECOND = 1000;
    public static final String DEFAULT_TIME = "  00  :  00  :  00  ";

    private String simTime = "";
    private CountDownTimer timer;

    private Paint borderPaint;
    private boolean border = false;

    public Student_CountDownTimeTextView(Context context) {
        super(context);
        init(context, null);
    }

    public Student_CountDownTimeTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public Student_CountDownTimeTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
//            TypedArray ta = context.obtainStyledAttributes(attrs, R.id.CountDownTimeTextView);
//            if (ta.hasValue(R.styleable.CountDownTimeTextView_border)) {
//                border = ta.getBoolean(R.styleable.CountDownTimeTextView_border, false);
                borderPaint = new Paint();
                borderPaint.setDither(true);
                borderPaint.setAntiAlias(true);
                borderPaint.setStyle(Paint.Style.STROKE);
                borderPaint.setStrokeWidth(1f);
                borderPaint.setColor(Color.parseColor("#999999"));
//            }
        }
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable parcelable = super.onSaveInstanceState();
        SaveState ss = new SaveState(parcelable);
        ss.text = simTime;
        return ss;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        SaveState ss = (SaveState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        setText(ss.text);
    }

    static class SaveState extends View.BaseSavedState {
        private String text;

        public SaveState(Parcel source) {
            super(source);
            text = (String) source.readString();
        }

        public SaveState(Parcelable superState) {
            super(superState);
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeString(text);
        }

        public static final Parcelable.Creator<SaveState> CREATOR = new Creator<SaveState>() {
            @Override
            public SaveState createFromParcel(Parcel source) {
                return new SaveState(source);
            }

            @Override
            public SaveState[] newArray(int size) {
                return new SaveState[size];
            }
        };
    }

    public void setTime(long time, final OnFinishListener listener) {
        if (timer != null) {
            timer.cancel();
        }
        setText(DEFAULT_TIME);
        timer = new CountDownTimer(time, 1) {
            @Override
            public void onTick(long l) {
                if (l >= 1 * 60 * 60 * 1000) { //超过1小时
                    simTime = getTimeFromLong(l);
                } else {
                    simTime = getMillisecondsTimes(l);
                }
                setText(simTime);
            }

            @Override
            public void onFinish() {
                setText(DEFAULT_TIME);
                listener.onFinish();
            }
        };

    }

    private String getMillisecondsTimes(long l) {
        l = l % MS_IN_AN_HOUR;
        long minutes = l / MS_IN_A_MINUTE; //分
        l = l % MS_IN_A_MINUTE;
        long seconds = l / MS_IN_A_SECOND; //秒
        l = l % MS_IN_A_SECOND;
        long milliseconds = l / 10; //毫秒

        StringBuffer sb = new StringBuffer();
        if (minutes >= 10) {
            sb.append(BLANK + String.valueOf(minutes) + SPLIT);
        } else {
            sb.append(BLANK + "0" + String.valueOf(minutes) + SPLIT);
        }

        if (seconds >= 10) {
            sb.append(String.valueOf(seconds) + SPLIT);
        } else {
            sb.append("0" + String.valueOf(seconds) + SPLIT);
        }

        if (milliseconds >= 10) {
            sb.append(String.valueOf(milliseconds) + BLANK);
        } else {
            sb.append("0" + String.valueOf(milliseconds) + BLANK);
        }
        String result = sb.toString();
        return result;

    }

    public String getTimeFromLong(long diff) {
        diff = diff % MS_IN_A_DAY;
        long numHours = diff / MS_IN_AN_HOUR;
        diff = diff % MS_IN_AN_HOUR;
        long numMinutes = diff / MS_IN_A_MINUTE;
        diff = diff % MS_IN_A_MINUTE;
        long numSeconds = diff / MS_IN_A_SECOND;

        StringBuffer buf = new StringBuffer();
        if (numHours >= 10) { //hour
            buf.append(BLANK + numHours + SPLIT);
        } else if (numHours >= 0 && numHours < 10) {
            buf.append(BLANK + "0" + numHours + SPLIT);
        }

        if (numMinutes >= 10) { //minutes
            buf.append(numMinutes + SPLIT);
        } else if (numMinutes >= 0 && numMinutes < 10) {
            buf.append("0" + numMinutes + SPLIT);
        }

        if (numSeconds >= 10) { //seconds
            buf.append(numSeconds + BLANK);
        } else if (numSeconds >= 0 && numSeconds < 10) {
            buf.append("0" + numSeconds + BLANK);
        }

        String result = buf.toString();

        return result;
    }

    public void start() {
        timer.start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (border) {
            String text = getText().toString();
            String[] result = text.split(":");
            float[] length = new float[result.length];
            float splitWidth = getPaint().measureText(":");
            float blankWidth = getPaint().measureText("  ");

            if (result.length > 0) {
                float height = canvas.getHeight() - getPaddingBottom();
                for (int i = 0; i < result.length; i++) {
                    float value = getPaint().measureText(result[i]);
                    length[i] = value;
                }

                if (result.length == 3) {
                    RectF r1 = new RectF((int) (getPaddingLeft() + blankWidth / 2), getPaddingTop(), (int) (getPaddingLeft() + length[0] - blankWidth / 2), (int) (getPaddingTop() + height));
                    RectF r2 = new RectF((int) (r1.right + blankWidth + splitWidth), getPaddingTop(), (int) (r1.right + splitWidth + length[1]), (int) (getPaddingTop() + height));
                    RectF r3 = new RectF((int) (r2.right + blankWidth + splitWidth), getPaddingTop(), (int) (r2.right + splitWidth + length[2]), (int) (getPaddingTop() + height));
                    canvas.drawRoundRect(r1, RADIUS, RADIUS, borderPaint);
                    canvas.drawRoundRect(r2, RADIUS, RADIUS, borderPaint);
                    canvas.drawRoundRect(r3, RADIUS, RADIUS, borderPaint);
                } else {
                    return;
                }
            }
        }
    }

    public interface OnFinishListener {
        void onFinish();
    }
}