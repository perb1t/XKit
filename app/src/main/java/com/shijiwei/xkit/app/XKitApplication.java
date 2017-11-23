package com.shijiwei.xkit.app;

import android.app.Activity;
import android.app.Application;

import com.yanzhenjie.nohttp.InitializationConfig;
import com.yanzhenjie.nohttp.Logger;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.cache.DBCacheStore;
import com.yanzhenjie.nohttp.cache.DiskCacheStore;
import com.yanzhenjie.nohttp.cookie.DBCookieStore;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shijiwei on 2017/8/31.
 *
 * @VERSION 1.0
 */

public class XKitApplication extends Application {

    private static XKitApplication application = new XKitApplication();
    private static List<Activity> actStack = new ArrayList<>();


    @Override
    public void onCreate() {
        super.onCreate();

        /* 初始化网络框架 */
        initialNoHttp();

    }


    /**
     * Get a singleton for your application
     *
     * @return
     */
    public static XKitApplication getApplication() {
        if (application == null) application = new XKitApplication();
        return application;
    }

    /**
     * Add activities to stack
     *
     * @param act
     */
    public void addAct2Stack(Activity act) {
        if (!actStack.contains(act))
            actStack.add(act);
    }

    /**
     * Remove activities from stack
     *
     * @param act
     */
    public void removeActFromStack(Activity act) {
        if (actStack.contains(act)) {
            actStack.remove(act);
        }
    }

    /**
     * Exit the application
     */
    public void exit() {
        for (Activity act : actStack) if (act != null && !act.isFinishing()) act.finish();
        System.exit(0);
    }

    /**
     * Initialize nohttp
     */
    private void initialNoHttp() {

        NoHttp.initialize(
                InitializationConfig.newBuilder(this)
                        // 设置全局连接超时时间，单位毫秒
                        .connectionTimeout(30 * 1000)
                        // 设置全局服务器响应超时时间，单位毫秒
                        .readTimeout(30 * 1000)
                        // 保存到数据库,如果不使用缓存，设置false禁用
                        .cacheStore(new DBCacheStore(this).setEnable(true))
                        // 或者保存到SD卡
                        .cacheStore(new DiskCacheStore(this))
                        // 默认保存数据库DBCookieStore，开发者可以自己实现; 如果不维护cookie，设置false禁用。)
                        .cookieStore(new DBCookieStore(this).setEnable(false))
                        // 使用HttpURLConnection
//                        .networkExecutor(new URLConnectionNetworkExecutor())
                        // 使用OkHttp
//                        .networkExecutor(new OkHttpNetworkExecutor())
                        .build()
        );

        // 开启NoHttp的调试模式, 配置后可看到请求过程、日志和错误信息。
        Logger.setDebug(true);
        // 设置NoHttp打印Log的tag
        Logger.setTag("NoHttpSample");
    }

}
