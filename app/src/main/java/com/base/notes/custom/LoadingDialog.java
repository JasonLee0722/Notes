package com.base.notes.custom;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.base.notes.R;
import com.base.notes.utils.CommUtil;

/**
 * Created by Jason on 2016/10/25.
 */

public class LoadingDialog extends Dialog {
    private Context context;
    private Animation animation;

    public LoadingDialog(Context context) {
        super(context, R.style.loading_dialog); //自定义样式
        this.context = context;
    }

    public LoadingDialog(Context context, int themeResId) {
        super(context, themeResId);
        this.context = context;
    }

    public LoadingDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    private void init() {
        //以view的方式引入，然后回调activity方法，setContentView，实现自定义布局
        View view = LayoutInflater.from(context).inflate(R.layout.custom_loading_dialog, null);
        setContentView(view);
        LinearLayout llBlock = (LinearLayout) view.findViewById(R.id.llBlock); // 加载布局
        ImageView ivLoading = (ImageView) view.findViewById(R.id.ivLoading);

        // 设置背景（半透明及圆角）
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            llBlock.setBackgroundDrawable(CommUtil.getDrawable(CommUtil.dp2px(context, 5), Color.parseColor("#E0000000"), 0, 0));
        } else {
            llBlock.setBackground(CommUtil.getDrawable(CommUtil.dp2px(context, 5), Color.parseColor("#E0000000"), 0, 0));
        }

        // 设置旋转效果rotate
        animation = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setInterpolator(new LinearInterpolator());
        animation.setRepeatMode(Animation.RESTART);
        animation.setRepeatCount(Animation.INFINITE);
        animation.setDuration(1500);
        ivLoading.setAnimation(animation);

        //设置dialog大小
        Window dialogWindow = getWindow();
        dialogWindow.setGravity(Gravity.CENTER);//设置对话框位置
        WindowManager.LayoutParams params = dialogWindow.getAttributes(); // 获取对话框当前的参数值
        params.width = (CommUtil.dp2px(context, 100));
        params.height = (CommUtil.dp2px(context, 100));
        dialogWindow.setAttributes(params);
    }

    @Override
    public void show() {
        super.show();
        ImageView ivLoading = (ImageView) findViewById(R.id.ivLoading);
        ivLoading.setAnimation(animation);
    }
}
