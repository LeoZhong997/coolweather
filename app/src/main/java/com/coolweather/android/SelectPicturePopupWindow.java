package com.coolweather.android;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

/**
 * Created by ZhiQiang on 2017/6/25.
 */

public class SelectPicturePopupWindow extends PopupWindow implements PopupWindow.OnDismissListener, View.OnClickListener {

    private static final String TAG = "SelectPicturePopupWindo";

    private View mContentView;
    private Context mContext;
    private PictureCallback mPictureCallback;

    public SelectPicturePopupWindow(Context context, PictureCallback pictureCallback) {
        this.mContext = context;
        this.mPictureCallback = pictureCallback;

        init(mContext);
    }

    private void init(Context context) {
        mContentView = LayoutInflater.from(context).inflate(R.layout.popup_select_picture, null);
        mContentView.setOnClickListener(this);

        this.setContentView(mContentView);                             //设置SelectPicturePopupWindow弹出窗体的View
        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        this.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        this.update();

        ColorDrawable cd = new ColorDrawable(0000000000);               //实例化一个ColorDrawable颜色为半透明
        this.setBackgroundDrawable(cd);                                 //点Back建和其他地方使其消失，设置了这个才能触发onDismissListener，设置其他控件变化等操作

        TextView cancel = (TextView) mContentView.findViewById(R.id.popup_cancel_text_view);
        TextView gallery = (TextView) mContentView.findViewById(R.id.popup_select_from_gallery_text_view);
        TextView takePicture = (TextView) mContentView.findViewById(R.id.popup_take_picture_text_view);

        cancel.setOnClickListener(this);
        gallery.setOnClickListener(this);
        takePicture.setOnClickListener(this);
    }

    @Override
    public void onDismiss() {
        dismiss();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.popup_cancel_text_view:                   //取消
                break;
            case R.id.popup_select_from_gallery_text_view:    //照片图库
                mPictureCallback.onSelectFromGallery();
                break;
            case R.id.popup_take_picture_text_view:            //拍照
                mPictureCallback.onTakePicture();
                break;
        }
        dismiss();
    }

    public interface PictureCallback {
        void onTakePicture();
        void onSelectFromGallery();
    }

    public void show(View parent) {
        Log.d(TAG, "show has executed");
        if (!this.isShowing()) {
            showAtLocation(parent, Gravity.NO_GRAVITY, 0, 0);
        } else {
            this.dismiss();
        }
    }
}
