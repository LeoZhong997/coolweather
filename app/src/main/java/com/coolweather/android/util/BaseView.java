package com.coolweather.android.util;

import android.content.Context;
import android.graphics.Canvas;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by ZhiQiang on 2017/5/4.
 */

public abstract class BaseView extends View {

    private MyThread myThread;

    private boolean running = true;     //控制线程循环

    public BaseView(Context context) {
        super(context);
    }

    public BaseView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    protected abstract void onDrawSub(Canvas canvas);       //绘制图像
    protected abstract void logic();                        //逻辑方法，子类实现

    @Override
    protected void onDraw(Canvas canvas) {
        if (null == myThread) {
            myThread = new MyThread();
            myThread.start();
        } else {
            onDrawSub(canvas);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        running = false;                    //销毁view的时候设置成false，退出无限循环
        super.onDetachedFromWindow();
    }

    /**
     * 开启一个线程用于绘制UI
     */
    private class MyThread extends Thread{
        @Override
        public void run() {
            while (running){
                logic();
                postInvalidate();              //重新绘制，会调用onDraw
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
