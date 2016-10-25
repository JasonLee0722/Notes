package com.base.notes.widget;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.base.notes.R;;
import com.base.notes.custom.LoadingButton;

/**
 * Created by Jason on 2016/10/25.
 */

public class LoadingButtonActivity extends Activity {
    private LoadingButton btnLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_loading_button);
        btnLoading = (LoadingButton) findViewById(R.id.btnLoading);
        btnLoading.setText("确认", "加载中...");
    }

    public void commit(View v) {
        btnLoading.startLoading();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                btnLoading.endLoading();
            }
        }, 2000);
    }
}
