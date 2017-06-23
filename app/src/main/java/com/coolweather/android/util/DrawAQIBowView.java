package com.coolweather.android.util;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;

import com.coolweather.android.R;

/**
 * Created by ZhiQiang on 2017/5/4.
 */

public class DrawAQIBowView extends BaseView {

    private static final String TAG = "DrawAQIBowView";

    private Paint paint;
    private int viewW, viewH;
    private RectF extRect;
    private RectF intRect;
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
        paint.setAntiAlias(true);   //抗锯齿
        final View view = getRootView();
        ViewTreeObserver vto = view.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                viewW = view.getWidth();
                viewH = view.getHeight();
                extRect = new RectF(0, 0, viewW, viewW);
                int gap = viewW * 3 / 8;
                intRect = new RectF(extRect.centerX() - gap, extRect.centerY() - gap, extRect.centerX() + gap, extRect.centerY() + gap);
                LogUtil.d(TAG, "AQIBowView: " + view.toString() + "\n" + "viewW :" + viewW + "\n" + "viewH: " + viewH);
            }
        });
    }

    @Override
    protected void onDrawSub(Canvas canvas) {
        paint.setAntiAlias(true);
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
