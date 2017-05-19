package com.coolweather.android.util;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.util.AttributeSet;

/**
 * Created by ZhiQiang on 2017/5/4.
 */

public class DrawSunBowView extends BaseView {

    private static final String TAG = "DrawSunBowView";

    private boolean running = false;
    private Paint paint;
    private RectF extRect = new RectF(50, 50, 800, 800);
    private int startAngle = 180;
    private int sweepAngle = 180;
    private int currentAngle = 0;
    private String sunRise;
    private String sunSet;
    private String update;
    private float updateTime;
    private float sunRiseTime;
    private float sunSetTime;
    private float sunAngle = 0;


    public DrawSunBowView(Context context) {
        super(context);
        init();
    }

    public DrawSunBowView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setAntiAlias(true);   //抗锯齿
    }

    @Override
    protected void onDrawSub(Canvas canvas) {
        RectF sunRect;
        if (running) {
            sunRect = getSunRect();

            paint.setStyle(Paint.Style.FILL);
            paint.setColor(Color.LTGRAY);
            canvas.drawArc(extRect, startAngle, sweepAngle, true, paint);

            paint.setStyle(Paint.Style.FILL);
            paint.setColor(Color.argb(73, 0, 0, 0));
            canvas.drawRect(extRect.left, extRect.top, sunRect.centerX(), (extRect.bottom + extRect.top) / 2, paint);

            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(Color.WHITE);
            paint.setStrokeWidth(8);
            canvas.drawArc(extRect, startAngle, sweepAngle, false, paint);

            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(Color.WHITE);
            paint.setStrokeWidth(4);
            canvas.drawArc(getSunRect(), 0, 360, true, paint);
        }

    }

    @Override
    protected void logic() {
        if (running) {
            if (currentAngle == (int) sunAngle) {
                return;
            } else if (currentAngle < sunAngle / 2) {
                currentAngle += 1;
            } else if (currentAngle < sunAngle) {
                currentAngle += 2;
            } else {
                currentAngle = (int) sunAngle;
            }
        }
    }

    private RectF getSunRect() {
        RectF sunRect;
        float sunR = extRect.width() / 2;
        float sunXO = extRect.centerX();
        float sunYO = extRect.centerY();
        float sunX = (float) (sunXO - (Math.cos(Math.toRadians(currentAngle)) * sunR));
        float sunY = (float) (sunYO - (Math.sin(Math.toRadians(currentAngle)) * sunR));
        int sunWidth = 20;

        sunRect = new RectF(sunX - sunWidth, sunY - sunWidth, sunX + sunWidth, sunY + sunWidth);
        return sunRect;
    }

    private void setSunAngle() {
        int hour = 0;
        int minute = 0;

        hour = Integer.parseInt(sunRise.split(":")[0]);
        minute = Integer.parseInt(sunRise.split(":")[1]);
        sunRiseTime = hour * 60 + minute;

        hour = Integer.parseInt(sunSet.split(":")[0]);
        minute = Integer.parseInt(sunSet.split(":")[1]);
        sunSetTime = hour * 60 + minute;

        hour = Integer.parseInt(update.split(":")[0]);
        minute = Integer.parseInt(update.split(":")[1]);
        updateTime = hour * 60 + minute;

        sunAngle = ((updateTime - sunRiseTime) / (sunSetTime - sunRiseTime)) * 180;

        if (sunAngle > 180) {
            sunAngle = 180;
        }

        LogUtil.d(TAG, "sunRiseTime " + sunRiseTime + " sunSetTime " + sunSetTime + " updateTime " + updateTime + " sunAngle " + sunAngle);
    }

    public void start() {
        setSunAngle();
        currentAngle = 0;
        running = true;
    }

    public String getSunRise() {
        return sunRise;
    }

    public void setSunRise(String sunRise) {
        this.sunRise = sunRise;
        LogUtil.d(TAG, "sunRiseTime " + sunRise + " sunSetTime " + sunSet + " updateTime " + update);
    }

    public String getSunSet() {
        return sunSet;
    }

    public void setSunSet(String sunSet) {
        this.sunSet = sunSet;
        LogUtil.d(TAG, "sunRiseTime " + sunRise + " sunSetTime " + sunSet + " updateTime " + update);
    }

    public String getUpdate() {
        return update;
    }

    public void setUpdate(String update) {
        this.update = update;
        LogUtil.d(TAG, "sunRiseTime " + sunRise + " sunSetTime " + sunSet + " updateTime " + update);
    }
}
