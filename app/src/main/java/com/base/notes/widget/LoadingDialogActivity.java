package com.base.notes.widget;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
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

public class LoadingDialogActivity extends Activity {
    private Dialog mLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_loading_dialog);
        init();
    }

    private void init() {
//       mLoading = new LoadingDialog(this,R.style.loading_dialog);
        mLoading = createLoadingDialog();
    }

    public void show(View view) {
        mLoading.show();
    }

    public void hide(View view) {
        mLoading.hide();
    }

    private Dialog createLoadingDialog() {
        View view = LayoutInflater.from(this).inflate(R.layout.custom_loading_dialog, null);
        LinearLayout llBlock = (LinearLayout) view.findViewById(R.id.llBlock); // 加载布局
        final ImageView ivLoading = (ImageView) view.findViewById(R.id.ivLoading);

        // 设置背景（半透明及圆角）
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            llBlock.setBackgroundDrawable(CommUtil.getDrawable(CommUtil.dp2px(this, 5), Color.parseColor("#E0000000"), 0, 0));
        } else {
            llBlock.setBackground(CommUtil.getDrawable(CommUtil.dp2px(this, 5), Color.parseColor("#E0000000"), 0, 0));
        }

        // 设置旋转效果rotate
        final Animation animation = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setInterpolator(new LinearInterpolator());
        animation.setRepeatMode(Animation.RESTART);
        animation.setRepeatCount(Animation.INFINITE);
        animation.setDuration(1500);
        ivLoading.setAnimation(animation);

        Dialog dialog = new Dialog(this, R.style.loading_dialog); // 自定义dialog样式
        dialog.setContentView(view,
                new LinearLayout.LayoutParams(CommUtil.dp2px(this, 100), CommUtil.dp2px(this, 100)));

        // 需要重新启用动画
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                ivLoading.startAnimation(animation);
            }
        });

        return dialog;
    }
}
