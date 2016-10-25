package com.base.notes.custom;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.base.notes.R;

/**
 * Created by Jason on 2016/8/29.
 * 这是一个继承了LinearLayout的按钮，主要封装了如下功能：
 * 1.封装了背景选择器Selector
 * 2.封装了加载动画
 */
public class LoadingButton extends LinearLayout {
    private Context mContext;
    private TextView mTextView;
    private ImageView mImageView;
    private RotateAnimation mRotateAnimation;
    private String textDefault = "登录";
    private String textLoading = "验证中...";

    private static final String COLOR_NORMAL = "#30AAE9";
    private static final String COLOR_PRESSED = "#399CD5";
    private static final String COLOR_DISABLED = "#8ACDF5";

    private static final int STATE_NORMAL = 1;
    private static final int STATE_LOADING = 2;
    private static final int STATE_DISABLED = 3;
    public int state = STATE_NORMAL;

    public LoadingButton(Context context) {
        this(context,null);
    }

    public LoadingButton(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public LoadingButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        initialize();
    }

    /**
     * 初始化动画、文本、图片
     */
    private void initialize() {
        this.setOrientation(HORIZONTAL);
        this.setGravity(Gravity.CENTER);
        this.setClickable(true);

        //初始化加载动画
        mRotateAnimation = new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        mRotateAnimation.setRepeatCount(Animation.INFINITE);
        mRotateAnimation.setRepeatMode(Animation.RELATIVE_TO_SELF);
        mRotateAnimation.setInterpolator(new LinearInterpolator());
        mRotateAnimation.setDuration(1000);

        //初始化按钮背景Selector
        setStateNormal();

        //初始化TextView
        mTextView = new TextView(mContext);
        mTextView.setText(textDefault);
        mTextView.setTextColor(Color.WHITE);

        //初始化ImageView
        mImageView = new ImageView(mContext);
        mImageView.setImageResource(R.mipmap.ic_loading);
        mImageView.setPadding(dp2px(10), 0, dp2px(10), 0);
        mImageView.setVisibility(GONE);

        this.addView(mTextView, 0);
        this.addView(mImageView, 1);
    }

    /**
     * 设置文本
     */
    public void setText(String defaultText, String loadingText) {
        if (!defaultText.isEmpty()) {
            textDefault = defaultText;
            mTextView.setText(textDefault);
        }
        if (!loadingText.isEmpty()) {
            textLoading = loadingText;
        }
    }

    /**
     * 开始加载动画
     */
    public void startLoading() {
        if (state == STATE_NORMAL) {
            mImageView.setVisibility(VISIBLE);
            mImageView.startAnimation(mRotateAnimation);
            mTextView.setText(textLoading);
            state = STATE_LOADING;
        }
    }

    /**
     * 结束加载动画
     */
    public void endLoading() {
        if (state == STATE_LOADING) {
            mImageView.clearAnimation();
            mImageView.setVisibility(GONE);
            mTextView.setText(textDefault);
            state = STATE_NORMAL;
        }
    }

    /**
     * 按钮恢复为正常状态
     */
    public void setStateNormal() {
        Drawable normalDraw = getDrawable(dp2px(5), Color.parseColor(COLOR_NORMAL), 0, 0);
        Drawable pressedDraw = getDrawable(dp2px(5), Color.parseColor(COLOR_PRESSED), 0, 0);
        setBackgroundInDifferentVertion(getSelector(normalDraw, pressedDraw));
        state = STATE_NORMAL;
    }

    /**
     * 按钮变为不可用状态
     */
    public void setStateDisabled() {
        Drawable disabledDraw = getDrawable(dp2px(5), Color.parseColor(COLOR_DISABLED), 0, 0);
        setBackgroundInDifferentVertion(disabledDraw);
        state = STATE_DISABLED;
    }

    //region 公用方法

    /**
     * 设置shape
     */
    private GradientDrawable getDrawable(int radius, int fillColor, int strokeWidth, int strokeColor) {
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setCornerRadius(radius);
        gradientDrawable.setColor(fillColor);
        gradientDrawable.setStroke(strokeWidth, strokeColor);
        return gradientDrawable;
    }

    /**
     * 设置背景选择器
     */
    private StateListDrawable getSelector(Drawable normalDraw, Drawable pressedDraw) {
        StateListDrawable stateListDrawable = new StateListDrawable();
        stateListDrawable.addState(new int[]{android.R.attr.state_pressed}, pressedDraw);
        stateListDrawable.addState(new int[]{}, normalDraw);
        return stateListDrawable;
    }

    /**
     * 设置背景
     */
    public void setBackgroundInDifferentVertion(Drawable drawable) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            this.setBackgroundDrawable(drawable);
        } else {
            this.setBackground(drawable);
        }
    }

    /**
     * dp转px
     */
    private int dp2px(float dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dpVal, mContext.getResources().getDisplayMetrics());
    }
    //endregion
}
