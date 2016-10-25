package com.base.notes;

import android.app.ListActivity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends ListActivity {
    private final static String PATH = "com.base.notes.Path";

    /*
     * 主要是通过对Activity的label信息进行分析，从而将Activity进行分类并通过ListView显示出来。
     * 例如<activity android:name=".app.HelloWorld" android:label="@string/activity_hello_world">的label为
     * App/Activity/Hello World，程序运行的结果是一级目录含有App选项，点击App出现的二级目录含有Activity选项，
     * 点击Activity出现的三级目录含有Hello World选项，点击Hello World会出现Hello World这个Activity运行的结果
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        /*
         * 当程序启动要显示一级目录（App, Content, ...）时，Intent对象里绑定的附加信息path为空
         * 当点击一级目录选项进入二级目录（例如App下的Activity, Alarm, ...）时，path就不为空了（例如为App）
         */
        String path = intent.getStringExtra(PATH);

        if (path == null)
            path = "";

        // 通过adapter为当前ListActivity传递数据
        setListAdapter(new SimpleAdapter(this, getData(path),
                android.R.layout.simple_list_item_1, new String[]{"title"}, new int[]{android.R.id.text1}));
        // 允许当前ListView可以根据用户输入的值进行过滤
        getListView().setTextFilterEnabled(true);
    }

    /*
     * 获取应用列表，包括一级目录，二级目录，等等
     */
    private List getData(String prefix) {
        List<Map> myData = new ArrayList<Map>();
        /*
         * 获取在AndroidManifest.xml文件中的Intent Filter里配置了
         * Intent.ACTION_MAIN和Intent.CATEGORY_SAMPLE_CODE的所有Activity
         */
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_SAMPLE_CODE);
        PackageManager pm = getPackageManager();
        List<ResolveInfo> list = pm.queryIntentActivities(mainIntent, 0);

        if (null == list)
            return myData;

        String[] prefixPath;
        // 当要显示一级目录时，prefix为空字符串；当点击App进入要显示二级目录时，prefix为App；
        if (prefix.equals("")) {
            prefixPath = null;
        } else {
            prefixPath = prefix.split("/");
        }

        int len = list.size();

        Map<String, Boolean> entries = new HashMap<>();
        String pkgName = getPackageName();
        for (int i = 0; i < len; i++) {
            ResolveInfo info = list.get(i);
            if (!info.activityInfo.packageName.equals(pkgName))
                continue;
            // 获取Activity的label信息，例如App/Activity/Hello World
            CharSequence labelSeq = info.loadLabel(pm);
            String lable = labelSeq != null ? labelSeq.toString() : info.activityInfo.name;
            if (prefix.length() == 0 || lable.startsWith(prefix)) {
                // 例如labelPath = [App, Activity, Hello World]
                String[] labelPath = lable.split("/");
                // 例如要显示一级目录时nextLabel = "App"，要显示二级目录时，nextLabel = "Activity"
                String nextLabel = prefixPath == null ? labelPath[0] : labelPath[prefixPath.length];
                // 当要显示的label为Activity而非目录时，需要为label绑定跳转到相关Activity的Intent
                if ((prefixPath != null ? prefixPath.length : 0) == labelPath.length - 1) {
                    addItem(myData, nextLabel, activityIntent(
                            info.activityInfo.applicationInfo.packageName,
                            info.activityInfo.name));
                } else {
                    if (entries.get(nextLabel) == null) {
                        addItem(myData, nextLabel, browseIntent(prefix.equals("") ? nextLabel : prefix + "/" + nextLabel));
                        entries.put(nextLabel, true);
                    }
                }
            }
        }

        // 排序
        Collections.sort(myData, sDisplayNameComparator);
        return myData;
    }

    private final static Comparator<Map> sDisplayNameComparator = new Comparator<Map>() {
        private final Collator collator = Collator.getInstance();

        @Override
        public int compare(Map lhs, Map rhs) {
            return collator.compare(lhs.get("title"), rhs.get("title"));
        }
    };

    /*
     * 将符合条件的选项加入List中
     */
    protected void addItem(List<Map> data, String name, Intent intent) {
        Map<String, Object> temp = new HashMap<>();
        temp.put("title", name);
        temp.put("intent", intent);
        data.add(temp);
    }

    /*
     * 为label绑定跳转到相关Activity的Intent
     */
    protected Intent activityIntent(String pkg, String componentName) {
        Intent result = new Intent();
        result.setClassName(pkg, componentName);
        return result;
    }

    /*
     * 为label绑定跳转到当前Activity的Intent，附加信息path为当前目录
     * 例如为Activity这个label绑定的Intent中附加值为App/Activity
     */
    protected Intent browseIntent(String path) {
        Intent result = new Intent();
        result.setClass(this, MainActivity.class);
        result.putExtra(PATH, path);
        return result;
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        Map map = (Map) l.getItemAtPosition(position);

        Intent intent = (Intent) map.get("intent");
        startActivity(intent);
    }
}
