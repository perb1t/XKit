package com.shijiwei.xkit.utility.crash;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;
import android.util.Log;

import com.shijiwei.xkit.app.XKitApplication;
import com.shijiwei.xkit.utility.file.FileUtility;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * UncaughtException处理类,当程序发生Uncaught异常的时候,有该类来接管程序,并记录发送错误报告.
 * Created by shijiwei on 2017/6/5.
 */
public class CrashHandler implements UncaughtExceptionHandler {

    private static final String TAG = "CrashHandler";

    public static boolean ENABLE_EXCEPTION_UPLOAD_SERVER = false;
    public static boolean ENABLE_WRITE_EXCEPTION_TO_FILE = true;


    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    // CrashHandler实例
    private static CrashHandler mInstance = new CrashHandler();
    // 程序的Context对象
    private Context mContext;
    // 系统默认的UncaughtException处理类
    private UncaughtExceptionHandler mDefaultHandler;
    // 用来存储设备信息和异常信息
    private Map<String, String> deviceInfos = new HashMap<>();


    /**
     * 保证只有一个CrashHandler实例
     */
    private CrashHandler() {
    }

    /**
     * 获取CrashHandler实例 ,单例模式
     */
    public synchronized static CrashHandler getInstance() {
        return mInstance;
    }

    /**
     * 初始化
     *
     * @param context
     */
    public void init(Context context) {
        mContext = context;
        // 获取系统默认的UncaughtException处理器
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        // 设置该CrashHandler为程序的默认处理器
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    /**
     * 出现应用级异常时的处理---当UncaughtException发生时会转入该函数来处理
     */
    @Override
    public void uncaughtException(final Thread thread, final Throwable ex) {

        // 错误LOG
        //ex.printStackTrace();
        Log.e(TAG, ex.getMessage(), ex);

        new Thread(new Runnable() {

            public void run() {

                Looper.prepare();

                handleException(ex);

                /*退出栈里的activity*/
                XKitApplication.getApplication().exit();

                /*1表示这个程序是非正常退出, 0表示正常退出*/
                System.exit(1);

                Looper.loop();
            }

        }).start();

    }

    /**
     * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成.
     *
     * @param ex
     * @return true:如果处理了该异常信息;否则返回false.
     */
    private boolean handleException(Throwable ex) {

        if (ex == null) {
            return false;
        }

        // 收集设备参数信息
        collectDeviceInfo(mContext);
        // 保存日志文件
        saveCrashInfo2File(ex);

        return true;
    }

    /**
     * 收集设备参数信息
     *
     * @param ctx
     */
    public void collectDeviceInfo(Context ctx) {
        try {
            PackageManager pm = ctx.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(ctx.getPackageName(),
                    PackageManager.GET_ACTIVITIES);
            if (pi != null) {
                String versionName = pi.versionName == null ? "null"
                        : pi.versionName;
                String versionCode = pi.versionCode + "";
                deviceInfos.put("versionName", versionName);
                deviceInfos.put("versionCode", versionCode);
            }

            Field[] fields = Build.class.getDeclaredFields();
            for (Field field : fields) {
                try {
                    field.setAccessible(true);
                    deviceInfos.put(field.getName(), field.get(null).toString());
                    //LogUtils.d(TAG, "field.getName===" + field.getName() + " : " + field.get(null));
                } catch (Exception e) {
                    Log.e(TAG, "an error occured when collect crash info", e);
                }
            }
            deviceInfos.put("TIME", sdf.format(System.currentTimeMillis()));

        } catch (Exception e) {
            Log.e(TAG, "an error occured when collect package info");
            e.printStackTrace();
        }
    }

    /**
     * 保存错误信息到文件中
     *
     * @param ex
     * @return 返回文件名称, 便于将文件传送到服务器
     */
    public void saveCrashInfo2File(Throwable ex) {

        StringBuffer sb = new StringBuffer();
        sb.append("设备信息:" + "\n");
        for (Map.Entry<String, String> entry : deviceInfos.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            sb.append(key + "=" + value + "\n");
        }
        sb.append("" + "\n");

        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        ex.printStackTrace(printWriter);
        Throwable cause = ex.getCause();
        while (cause != null) {
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }
        printWriter.close();

        String result = writer.toString();
        sb.append("异常信息:" + "\n");
        sb.append(result);

        //错误日志上传到服务器
        if (ENABLE_EXCEPTION_UPLOAD_SERVER) {

        }

        //错误日志输出到文件
        if (ENABLE_WRITE_EXCEPTION_TO_FILE) {
            try {

                String time = sdf.format(System.currentTimeMillis()).replace(":", "-").replace(" ", "-");
                String fileName = "crash-" + time + ".log";
                if (Environment.getExternalStorageState().equals(
                        Environment.MEDIA_MOUNTED)) {

                    File dir = new File(FileUtility.APP_CRASH_LOG_PATH);

                    if (!dir.exists())
                        dir.mkdirs();

                    FileOutputStream fos = new FileOutputStream(
                            FileUtility.APP_CRASH_LOG_PATH + File.separator + fileName);
                    fos.write(sb.toString().getBytes());
                    fos.close();
                }

            } catch (Exception e) {
                Log.e(TAG, "CrashHandler saveCrashInfo2File: an error occured while writing file...");
                e.printStackTrace();
            }
        }

    }
}
