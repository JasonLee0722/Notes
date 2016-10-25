package com.base.notes.widget;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.base.notes.R;
import com.base.notes.custom.CommAdapter;
import com.base.notes.custom.ViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jason on 2016/10/25.
 */

public class PopupMenuActivity extends Activity {
    private List<String> mData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_popup_menu);
        getData();
    }

    private void getData() {
        mData = new ArrayList<>();
        mData.add("--近卫--");
        mData.add("水晶室女");
        mData.add("船长");
        mData.add("撼地神牛");
        mData.add("矮人狙击手");
        mData.add("--天灾--");
        mData.add("恶魔巫师");
        mData.add("痛苦女王");
        mData.add("遗忘法师");
        mData.add("蝙蝠骑士");
    }


    public void showMenu(View v) {
        View view = LayoutInflater.from(this).inflate(R.layout.custom_popup_menu, null);
        final PopupWindow popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
//        popupWindow.setOutsideTouchable(true);
//        popupWindow.setTouchable(true);
        //点击 back 键的时候，窗口会自动消失
        popupWindow.setBackgroundDrawable(new BitmapDrawable(getResources(), (Bitmap) null));

        ListView listView = (ListView) view.findViewById(R.id.lvPop);
        listView.setAdapter(new CommAdapter<String>(this, mData, R.layout.list_popup_item) {
            @Override
            public void convert(ViewHolder holder, String s) {
                holder.setText(R.id.tvName, s);
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String name = (String) parent.getAdapter().getItem(position);
                Toast.makeText(PopupMenuActivity.this, name, Toast.LENGTH_SHORT).show();
                popupWindow.dismiss();
            }
        });
        popupWindow.showAsDropDown(v);
    }

}
