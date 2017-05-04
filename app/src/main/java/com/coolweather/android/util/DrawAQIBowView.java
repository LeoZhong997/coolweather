package com.coolweather.android.util;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import com.coolweather.android.R;

/**
 * Created by ZhiQiang on 2017/5/4.
 */

public class DrawAQIBowView extends BaseView {

    private Paint paint;
    private RectF extRect = new RectF(0, 0, 350, 350);
    private RectF intRect = new RectF(extRect.centerX() - 120, extRect.centerY() - 120, extRect.centerX() + 120, extRect.centerY() + 120);
    private int startAngle = 180;
    private int sweepAngle = 180;
    private float aqiQuality = 100;
    private float aqiQualityMax = 500;
    private float aqiAngle;

    public DrawAQIBowView(Context context) {
        super(context);
        init();
    }

    public DrawAQIBowView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    /**
     * 初始化画笔
     */
    private void init() {
        paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setTextSize(50);
    }

    @Override
    protected void onDrawSub(Canvas canvas) {
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawArc(extRect, startAngle, aqiAngle, true, paint);
        paint.setColor(Color.BLACK);
        canvas.drawArc(extRect, startAngle + aqiAngle, sweepAngle - aqiAngle, true, paint);
        paint.setColor(Color.DKGRAY);
        canvas.drawArc(intRect, startAngle, sweepAngle, true, paint);
    }

    @Override
    protected void logic() {
        aqiAngle = (aqiQuality / aqiQualityMax) * 180;
    }

    public float getAqiQuality() {
        return aqiQuality;
    }

    public void setAqiQuality(float aqiQuality) {
        this.aqiQuality = aqiQuality;
    }
}
