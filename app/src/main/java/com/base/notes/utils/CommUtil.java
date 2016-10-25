package com.base.notes.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


/**
 * Created by Jason on 2016/7/19.
 */
public class CommUtil {

    private CommUtil() {
        /* cannot be instantiated */
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    //region 网络相关

    /*
    * 判断网络是否连接
    */
    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (null != cm) {
            NetworkInfo info = cm.getActiveNetworkInfo();
            if (null != info && info.isConnected()) {
                if (info.getState() == NetworkInfo.State.CONNECTED) {
                    return true;
                }
            }
        }
        return false;
    }

    /*
    *判断是否是wifi连接
    */
    public static boolean isWifi(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (null != cm && isNetworkConnected(context)) {
            return cm.getActiveNetworkInfo().getType() == ConnectivityManager.TYPE_WIFI;
        }
        return false;
    }

    /**
     * 打开网络设置界面
     */
    public static void openSetting(Activity activity) {
        Intent intent = null;
        //判断手机系统的版本  即API大于10 就是3.0或以上版本
        if (android.os.Build.VERSION.SDK_INT > 10) {
            intent = new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS);
        } else {
            intent = new Intent();
            ComponentName component = new ComponentName("com.android.settings", "com.android.settings.WirelessSettings");
            intent.setComponent(component);
            intent.setAction("android.intent.action.VIEW");
        }
        activity.startActivity(intent);
    }

    /**
     * post请求方式，完成form表单的提交
     */
    public static void post(String _url, String para) {
        HttpURLConnection conn = null;
        try {
            URL url = new URL(_url);
//            para = new String("grant_type=client_credentials&client_id=admin&client_secret=admin&scope=admin");
            //1.得到HttpURLConnection实例化对象
            conn = (HttpURLConnection) url.openConnection();
            //2.设置请求方式
            conn.setRequestMethod("POST");
            //3.设置post提交内容的类型和长度
            /*
             * 只有设置contentType为application/x-www-form-urlencoded，
             * servlet就可以直接使用request.getParameter("username");直接得到所需要信息
             */
            conn.setRequestProperty("contentType", "application/x-www-form-urlencoded");
            conn.setRequestProperty("Content-Length", String.valueOf(para.getBytes().length));
            //默认为false
            conn.setDoOutput(true);
            //4.向服务器写入数据
            conn.getOutputStream().write(para.getBytes());
            //5.得到服务器相应
            if (conn.getResponseCode() == 200) {
                System.out.println("服务器已经收到表单数据！");
            } else {
                int a = conn.getResponseCode();
                System.out.println("请求失败！");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //6.释放资源
            if (conn != null) {
                //关闭连接 即设置 http.keepAlive = false;
                conn.disconnect();
            }
        }
    }
    //endregion

    //region 单位转换

    /**
     * dp转px
     *
     * @param context
     * @param dpVal
     * @return
     */
    public static int dp2px(Context context, float dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dpVal, context.getResources().getDisplayMetrics());
    }

    /**
     * sp转px
     *
     * @param context
     * @param spVal
     * @return
     */
    public static int sp2px(Context context, float spVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                spVal, context.getResources().getDisplayMetrics());
    }

    /**
     * px转dp
     *
     * @param context
     * @param pxVal
     * @return
     */
    public static float px2dp(Context context, float pxVal) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (pxVal / scale);
    }

    /**
     * px转sp
     *
     * @param context
     * @param pxVal
     * @return
     */
    public static float px2sp(Context context, float pxVal) {
        return (pxVal / context.getResources().getDisplayMetrics().scaledDensity);
    }
    //endregion

    //region 字符格式

    /**
     * 判断字符串是否为空
     */
    public static boolean isEmpty(String s) {
        if (null == s)
            return true;
        if (s.length() == 0)
            return true;
        if (s.trim().length() == 0)
            return true;
        return false;
    }

    /**
     * 是否符合电话号码格式
     *
     * @param mobiles
     * @return
     */
    public static boolean isMobileNO(String mobiles) {
    /*
    移动：134、135、136、137、138、139、150、151、157(TD)、158、159、187、188
    联通：130、131、132、152、155、156、185、186
    电信：133、153、180、189、（1349卫通）
    总结起来就是第一位必定为1，第二位必定为3或5或8，其他位置的可以为0-9
    */
        //"^((13[0-9])|(15[^4,//D])|(18[0,5-9]))//d{8}$"
        String telRegex = "[1][358]\\d{9}";
        if (TextUtils.isEmpty(mobiles)) return false;
        else return mobiles.matches(telRegex);
    }

    /**
     * 获取文件扩展名
     *
     * @param filename
     * @return
     */
    public static String getExtensionName(String filename) {
        if ((filename != null) && (filename.length() > 0)) {
            int dot = filename.lastIndexOf('.');
            if ((dot > -1) && (dot < (filename.length() - 1))) {
                return filename.substring(dot + 1);
            }
        }
        return filename;
    }
    //endregion

    //region 资源相关

    /**
     * 获取资源
     *
     * @param resName
     * @return
     */
    public static int getResource(Context context, String resName) {
        int resId = context.getResources().getIdentifier(resName, "mipmap", context.getPackageName());
        //如果没有在"mipmap"下找到imageName,将会返回0
        return resId;
    }
    //endregion

    //region 键盘相关

    /**
     * 打卡软键盘
     */
    public static void openKeybord(Context mContext, EditText mEditText) {
        InputMethodManager imm = (InputMethodManager) mContext
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(mEditText, InputMethodManager.RESULT_SHOWN);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,
                InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    /**
     * 关闭软键盘
     */
    public static void closeKeybord(Context mContext, EditText mEditText) {
        InputMethodManager imm = (InputMethodManager) mContext
                .getSystemService(Context.INPUT_METHOD_SERVICE);

        imm.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);
    }
    //endregion

    //region 屏幕相关

    /**
     * 获得屏幕高度
     */
    public static int getScreenWidth(Context context) {
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.widthPixels;
    }

    /**
     * 获得屏幕宽度
     */
    public static int getScreenHeight(Context context) {
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.heightPixels;
    }

    /**
     * 获得状态栏的高度
     */
    public static int getStatusHeight(Context context) {

        int statusHeight = -1;
        try {
            Class<?> clazz = Class.forName("com.android.internal.R$dimen");
            Object object = clazz.newInstance();
            int height = Integer.parseInt(clazz.getField("status_bar_height")
                    .get(object).toString());
            statusHeight = context.getResources().getDimensionPixelSize(height);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return statusHeight;
    }

    /**
     * 获取当前屏幕截图，包含状态栏
     */
    public static Bitmap snapShotWithStatusBar(Activity activity) {
        View view = activity.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap bmp = view.getDrawingCache();
        int width = getScreenWidth(activity);
        int height = getScreenHeight(activity);
        Bitmap bp = null;
        bp = Bitmap.createBitmap(bmp, 0, 0, width, height);
        view.destroyDrawingCache();
        return bp;

    }

    /**
     * 获取当前屏幕截图，不包含状态栏
     */
    public static Bitmap snapShotWithoutStatusBar(Activity activity) {
        View view = activity.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap bmp = view.getDrawingCache();
        Rect frame = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        int statusBarHeight = frame.top;

        int width = getScreenWidth(activity);
        int height = getScreenHeight(activity);
        Bitmap bp = null;
        bp = Bitmap.createBitmap(bmp, 0, statusBarHeight, width, height
                - statusBarHeight);
        view.destroyDrawingCache();
        return bp;

    }
    //endregion

    //region 时间相关

    /**
     * 获取当前时间
     */

    public static String getCurrentTime(String format) {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
        String currentTime = sdf.format(date);
        return currentTime;
    }

    public static String getCurrentTime() {
        return getCurrentTime("yyyy-MM-dd  HH:mm:ss");
    }
    //endregion

    //region MD5加密
    public static String md5(String string) {
        byte[] hash;
        try {
            hash = MessageDigest.getInstance("MD5").digest(string.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Huh, MD5 should be supported?", e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Huh, UTF-8 should be supported?", e);
        }

        StringBuilder hex = new StringBuilder(hash.length * 2);
        for (byte b : hash) {
            if ((b & 0xFF) < 0x10) hex.append("0");
            hex.append(Integer.toHexString(b & 0xFF));
        }
        return hex.toString().toUpperCase();
    }
    //endregion

    //region 类型转换
    public static final int convertToInt(String value, int defaultValue) {
        if (value == null || "".equals(value.trim())) {
            return defaultValue;
        }
        try {
            return Integer.valueOf(value);
        } catch (Exception e) {
            try {
                return Double.valueOf(value).intValue();
            } catch (Exception e1) {
                return defaultValue;
            }
        }
    }

    public static <E> String list2String(List<E> list) throws IOException {
        //实例化一个ByteArrayOutputStream对象，用来装载压缩后的字节文件
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        //然后将得到的字符数据装载到ObjectOutputStream
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        //writeObject 方法负责写入特定类的对象的状态，以便相应的readObject可以还原它
        oos.writeObject(list);
        //最后，用Base64.encode将字节文件转换成Base64编码，并以String形式保存
        String listString = new String(Base64.encode(baos.toByteArray(), Base64.DEFAULT));
        //关闭oos
        oos.close();
        return listString;
    }

    public static String obj2Str(Object obj) throws IOException {
        if (obj == null) {
            return "";
        }
        //实例化一个ByteArrayOutputStream对象，用来装载压缩后的字节文件
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        //然后将得到的字符数据装载到ObjectOutputStream
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        //writeObject 方法负责写入特定类的对象的状态，以便相应的readObject可以还原它
        oos.writeObject(obj);
        //最后，用Base64.encode将字节文件转换成Base64编码，并以String形式保存
        String listString = new String(Base64.encode(baos.toByteArray(), Base64.DEFAULT));
        //关闭oos
        oos.close();
        return listString;
    }

    public static Object str2Obj(String str) throws StreamCorruptedException, IOException {
        byte[] mByte = Base64.decode(str.getBytes(), Base64.DEFAULT);
        ByteArrayInputStream bais = new ByteArrayInputStream(mByte);
        ObjectInputStream ois = new ObjectInputStream(bais);

        try {
            return ois.readObject();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;

    }

    public static <E> List<E> string2List(String str) throws StreamCorruptedException, IOException {
        byte[] mByte = Base64.decode(str.getBytes(), Base64.DEFAULT);
        ByteArrayInputStream bais = new ByteArrayInputStream(mByte);
        ObjectInputStream ois = new ObjectInputStream(bais);
        List<E> stringList = null;
        try {
            stringList = (List<E>) ois.readObject();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return stringList;
    }

    public static Map<String, Object> obj2map(Object obj) {
        if (obj == null) {
            return null;
        }
        Map<String, Object> result = new HashMap<String, Object>();
        Method[] methods = obj.getClass().getDeclaredMethods();
        for (Method method : methods) {
            try {
                if (method.getName().startsWith("get")) {
                    String field = method.getName();
                    field = field.substring(field.indexOf("get") + 3);
                    field = field.charAt(0) + field.substring(1);
                    Object value = method.invoke(obj, (Object[]) null);
                    result.put(field, null == value ? "" : value.toString());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }
    //endregion

    //region 设备信息
    public static String getDeviceId(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return tm.getDeviceId();
    }
    //endregion

    //region 界面跳转

    /**
     * 判断是否在后台运行
     *
     * @param context
     * @return
     */
    public static boolean isBackground(Context context) {
        ActivityManager activityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager
                .getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.processName.equals(context.getPackageName())) {
                /*
                BACKGROUND=400 EMPTY=500 FOREGROUND=100
                GONE=1000 PERCEPTIBLE=130 SERVICE=300 ISIBLE=200
                 */
                Log.i(context.getPackageName(), "此appimportace ="
                        + appProcess.importance
                        + ",context.getClass().getName()="
                        + context.getClass().getName());
                if (appProcess.importance != ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    Log.i(context.getPackageName(), "处于后台"
                            + appProcess.processName);
                    return true;
                } else {
                    Log.i(context.getPackageName(), "处于前台"
                            + appProcess.processName);
                    return false;
                }
            }
        }
        return false;
    }

    /**
     * 获取当前Activity名称
     */
    private String getCurrentActivityName(Context context) {
        ActivityManager activityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager
                .getRunningAppProcesses();
        String resulet = "";
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.processName.equals(context.getPackageName())) {
                /*
                BACKGROUND=400 EMPTY=500 FOREGROUND=100
                GONE=1000 PERCEPTIBLE=130 SERVICE=300 ISIBLE=200
                 */
                Log.i(context.getPackageName(), "此appimportace ="
                        + appProcess.importance
                        + ",context.getClass().getName()="
                        + context.getClass().getName());
                resulet = context.getClass().getName();
            }
        }
        return resulet;
    }

    /**
     * @param packageContext
     * @param action         含操作的Intent
     * @Description: 隐式启动, 跳转
     */
    public static void startActivityIntentSafe(Context packageContext, Intent action) {
        // Verify it resolves
        PackageManager packageManager = packageContext.getPackageManager();
        List activities = packageManager.queryIntentActivities(action,
                PackageManager.MATCH_DEFAULT_ONLY);
        boolean isIntentSafe = activities.size() > 0;
        // Start an activity if it's safe
        if (isIntentSafe) {
            packageContext.startActivity(action);
        }
    }

    /**
     * @param packageContext
     * @param cls
     * @param keys
     * @param values         手动改变int[] values类型,可以传递其它数据类型,就不重载了
     * @Description: 跳转, 带参数的方法;需要其它的数据类型,再继续重载吧,暂时只写这么多吧,意义不大
     */
    public static void toNextActivity(Context packageContext, Class<?> cls,
                                      String[] keys, int[] values) {
        Intent i = new Intent(packageContext, cls);
        for (int j = 0; j < values.length; j++) {
            i.putExtra(keys[j], values[j]);
        }
        packageContext.startActivity(i);

    }

    /**
     * @param packageContext from,一般传XXXActivity.this
     * @param cls            to,一般传XXXActivity.class
     * @Description: 跳转
     */
    public static void toNextActivity(Context packageContext, Class<?> cls) {
        Intent i = new Intent(packageContext, cls);
        packageContext.startActivity(i);
    }

    /**
     * @param packageContext
     * @param cls
     * @param keyvalues      需要传进去的String参数{{key1,values},{key2,value2}...}
     * @Description: 跳转, 带参数的方法;需要其它的数据类型,再继续重载吧
     */
    public static void toNextActivity(Context packageContext, Class<?> cls,
                                      String[][] keyvalues) {
        Intent i = new Intent(packageContext, cls);
        for (String[] strings : keyvalues) {
            i.putExtra(strings[0], strings[1]);
        }
        packageContext.startActivity(i);

    }

    public static void toNextActivityAndFinish(Context packageContext,
                                               Class<?> cls) {
        Intent i = new Intent(packageContext, cls);
        packageContext.startActivity(i);

        ((Activity) packageContext).finish();
    }

    public static void finish(Activity activity) {
        activity.finish();
    }
    //endregion

    //region 打开文件

    /**
     * 打开文件
     *
     * @param context
     * @param file
     */
    public static void openFile(Context context, File file) {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //设置intent的Action属性
        intent.setAction(Intent.ACTION_VIEW);
        //获取文件file的MIME类型
        String type = getMIMEType(file);
        //设置intent的data和Type属性。
        intent.setDataAndType(Uri.fromFile(file), type);
        //跳转
        CommUtil.startActivityIntentSafe(context, intent); //这里最好try一下，有可能会报错。 //比如说你的MIME类型是打开邮箱，但是你手机里面没装邮箱客户端，就会报错。
    }

    private static String getMIMEType(File file) {

        String type = "*/*";
        String fName = file.getName();
        //获取后缀名前的分隔符"."在fName中的位置。
        int dotIndex = fName.lastIndexOf(".");
        if (dotIndex < 0) {
            return type;
        }
    /* 获取文件的后缀名*/
        String end = fName.substring(dotIndex, fName.length()).toLowerCase();
        if (end == "") return type;
        //在MIME和文件类型的匹配表中找到对应的MIME类型。
        for (int i = 0; i < MIME_MapTable.length; i++) { //MIME_MapTable??在这里你一定有疑问，这个MIME_MapTable是什么？
            if (end.equals(MIME_MapTable[i][0]))
                type = MIME_MapTable[i][1];
        }
        return type;
    }

    private static final String[][] MIME_MapTable = {
            //{后缀名，MIME类型}
            {".txt", "text/plain"},
            {".doc", "application/msword"},
            {".docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document"},
            {".xls", "application/vnd.ms-excel"},
            {".xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"},
            {".ppt", "application/vnd.ms-powerpoint"},
            {".pptx", "application/vnd.openxmlformats-officedocument.presentationml.presentation"},
            {".pdf", "application/pdf"},
            {".bmp", "image/bmp"},
            {".gif", "image/gif"},
            {".jpeg", "image/jpeg"},
            {".jpg", "image/jpeg"}

//            {".3gp", "video/3gpp"},
//            {".apk", "application/vnd.android.package-archive"},
//            {".asf", "video/x-ms-asf"},
//            {".avi", "video/x-msvideo"},
//            {".bin", "application/octet-stream"},
//            {".c", "text/plain"},
//            {".class", "application/octet-stream"},
//            {".conf", "text/plain"},
//            {".cpp", "text/plain"},
//            {".exe", "application/octet-stream"},
//            {".gtar", "application/x-gtar"},
//            {".gz", "application/x-gzip"},
//            {".h", "text/plain"},
//            {".htm", "text/html"},
//            {".html", "text/html"},
//            {".jar", "application/java-archive"},
//            {".java", "text/plain"},
//            {".js", "application/x-javascript"},
//            {".log", "text/plain"},
//            {".m3u", "audio/x-mpegurl"},
//            {".m4a", "audio/mp4a-latm"},
//            {".m4b", "audio/mp4a-latm"},
//            {".m4p", "audio/mp4a-latm"},
//            {".m4u", "video/vnd.mpegurl"},
//            {".m4v", "video/x-m4v"},
//            {".mov", "video/quicktime"},
//            {".mp2", "audio/x-mpeg"},
//            {".mp3", "audio/x-mpeg"},
//            {".mp4", "video/mp4"},
//            {".mpc", "application/vnd.mpohun.certificate"},
//            {".mpe", "video/mpeg"},
//            {".mpeg", "video/mpeg"},
//            {".mpg", "video/mpeg"},
//            {".mpg4", "video/mp4"},
//            {".mpga", "audio/mpeg"},
//            {".msg", "application/vnd.ms-outlook"},
//            {".ogg", "audio/ogg"},
//            {".png", "image/png"},
//            {".pps", "application/vnd.ms-powerpoint"},
//            {".prop", "text/plain"},
//            {".rc", "text/plain"},
//            {".rmvb", "audio/x-pn-realaudio"},
//            {".rtf", "application/rtf"},
//            {".sh", "text/plain"},
//            {".tar", "application/x-tar"},
//            {".tgz", "application/x-compressed"},
//            {".wav", "audio/x-wav"},
//            {".wma", "audio/x-ms-wma"},
//            {".wmv", "audio/x-ms-wmv"},
//            {".wps", "application/vnd.ms-works"},
//            {".xml", "text/plain"},
//            {".z", "application/x-compress"},
//            {".zip", "application/x-zip-compressed"},
//            {"", "*/*"}
    };
    //endregion

    //region 缓存相关
    public static String getTotalCacheSize(Context context) throws Exception {
        long cacheSize = getFolderSize(context.getCacheDir());
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            cacheSize += getFolderSize(context.getExternalCacheDir());
        }
        return getFormatSize(cacheSize);
    }


    public static void clearAllCache(Context context) {
        deleteDir(context.getCacheDir());
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            deleteDir(context.getExternalCacheDir());
        }
    }

    private static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        return dir.delete();
    }

    // 获取文件
    //Context.getExternalFilesDir() --> SDCard/Android/data/你的应用的包名/files/ 目录，一般放一些长时间保存的数据
    //Context.getExternalCacheDir() --> SDCard/Android/data/你的应用包名/cache/目录，一般存放临时缓存数据
    public static long getFolderSize(File file) throws Exception {
        long size = 0;
        try {
            File[] fileList = file.listFiles();
            for (int i = 0; i < fileList.length; i++) {
                // 如果下面还有文件
                if (fileList[i].isDirectory()) {
                    size = size + getFolderSize(fileList[i]);
                } else {
                    size = size + fileList[i].length();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return size;
    }

    /**
     * 格式化单位
     *
     * @param size
     * @return
     */
    public static String getFormatSize(double size) {
        double kiloByte = size / 1024;
        if (kiloByte < 1) {
//            return size + "Byte";
            return "0K";
        }

        double megaByte = kiloByte / 1024;
        if (megaByte < 1) {
            BigDecimal result1 = new BigDecimal(Double.toString(kiloByte));
            return result1.setScale(2, BigDecimal.ROUND_HALF_UP)
                    .toPlainString() + "KB";
        }

        double gigaByte = megaByte / 1024;
        if (gigaByte < 1) {
            BigDecimal result2 = new BigDecimal(Double.toString(megaByte));
            return result2.setScale(2, BigDecimal.ROUND_HALF_UP)
                    .toPlainString() + "MB";
        }

        double teraBytes = gigaByte / 1024;
        if (teraBytes < 1) {
            BigDecimal result3 = new BigDecimal(Double.toString(gigaByte));
            return result3.setScale(2, BigDecimal.ROUND_HALF_UP)
                    .toPlainString() + "GB";
        }
        BigDecimal result4 = new BigDecimal(teraBytes);
        return result4.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString()
                + "TB";
    }
    //endregion

    //region 选择器相关
    /**
     * 设置shape
     */
    public static GradientDrawable getDrawable(int radius, int fillColor, int strokeWidth, int strokeColor) {
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setCornerRadius(radius);
        gradientDrawable.setColor(fillColor);
        gradientDrawable.setStroke(strokeWidth, strokeColor);
        return gradientDrawable;
    }

    /**
     * 设置背景选择器
     */
    public static StateListDrawable getSelector(Drawable normalDraw, Drawable pressedDraw) {
        StateListDrawable stateListDrawable = new StateListDrawable();
        stateListDrawable.addState(new int[]{android.R.attr.state_pressed}, pressedDraw);
        stateListDrawable.addState(new int[]{}, normalDraw);
        return stateListDrawable;
    }
    //endregion

    //region 自定义

    /**
     * 截取消息栏目第一个字（例如“学习园地”截取到“学”）
     *
     * @param word
     * @return
     */
    public static String getFirstWord(String word) {
        return (word == null || word.equals("") || word.length() == 0) ? "" :
                word.subSequence(0, 1).toString();
    }
    //endregion
}
