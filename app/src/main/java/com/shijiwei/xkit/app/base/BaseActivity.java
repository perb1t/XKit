package com.shijiwei.xkit.app.base;

import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.shijiwei.xkit.app.XKitApplication;
import com.shijiwei.xkit.nohttp.interfaces.HttpResponseListener;
import com.shijiwei.xkit.nohttp.interfaces.RequestListener;
import com.shijiwei.xkit.utility.display.DisplayUtility;
import com.shijiwei.xkit.utility.log.LogUtil;
import com.shijiwei.xkit.utility.network.NetworkUtility;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.rest.CacheMode;
import com.yanzhenjie.nohttp.rest.Request;
import com.yanzhenjie.nohttp.rest.RequestQueue;
import com.yanzhenjie.nohttp.rest.Response;

/**
 * Created by shijiwei on 2017/8/31.
 *
 * @VERSION 1.0
 */

public abstract class BaseActivity extends AppCompatActivity implements
        NetworkUtility.OnNetworkChangedListener,
        RequestListener {

    private static final String TAG = "BaseActivity";

    private XKitApplication mApplication;
    private NetworkUtility mNetworkUtility;

    public RequestQueue mQueue;

    private boolean isNetworkAvailable;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResId());
        initialStatusBar();
        mApplication = XKitApplication.getApplication();
        mApplication.addAct2Stack(this);
        mNetworkUtility = new NetworkUtility(this, this);
        mQueue = NoHttp.newRequestQueue();
        initialData(savedInstanceState);
        initialView();
        initialEvn();

    }

    @Override
    protected void onDestroy() {
        mNetworkUtility.unregisterReceiver(this);
        mApplication.removeActFromStack(this);
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        onBackKeyPressed();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    /**
     * Gets the resource id of the layout file for the current page
     * invoked in the onCreate () of the life cycle
     *
     * @return
     */
    public abstract int getLayoutResId();

    /**
     * Initialize data and declare data collections,invoked in the onCreate () of the life cycle
     */
    public abstract void initialData(Bundle savedInstanceState);

    /**
     * Initialize the view widget,invoked in the onCreate () of the life cycle
     */
    public abstract void initialView();

    /**
     * Initializes the view widget response callback event,invoked in the onCreate () of the life cycle
     */
    public abstract void initialEvn();

    /**
     * Interceptor system returns a key event
     */
    public abstract void onBackKeyPressed();

    /**
     * Monitor the network status of the device
     */
    public abstract void onNetworkStateChanged(int type, boolean isAvailable);


    @Override
    public void onNetworkChanged(int type, boolean isAvailable) {
        isNetworkAvailable = isAvailable;
        onNetworkStateChanged(type, isAvailable);
    }


    @Override
    public void onHttpStart(int what) {
        LogUtil.e(TAG + " onHttpStart ", " " + what);
    }

    @Override
    public void onHttpSucceed(int what, Response response) {
        LogUtil.e(TAG + " onHttpSucceed ", " " + what + "  " + response.get());
    }

    @Override
    public void onHttpFailed(int what, Response response) {
        LogUtil.e(TAG + " onHttpFailed ", " " + what + "  " + response.getException().getMessage());
    }

    @Override
    public void onHttpFinish(int what) {
        LogUtil.e(TAG + " onHttpFinish ", " " + what);
    }


    /**
     * Add network requests task to queues
     *
     * @param what
     * @param request
     */
    public void addTask2Queue(int what, Request request) {
//        if (isNetworkAvailable) {
        // net is available
        request.setCancelSign(this);
        request.setCacheMode(CacheMode.REQUEST_NETWORK_FAILED_READ_CACHE);
        mQueue.add(what, request, new HttpResponseListener(this));
//        } else {
        // net is not available

//        }

    }

    /**
     * Immersive status bar
     */
    private void initialStatusBar() {
        ViewGroup contentFrameLayout = (ViewGroup) findViewById(Window.ID_ANDROID_CONTENT);
        View parentView = contentFrameLayout.getChildAt(0);
        if (parentView != null && Build.VERSION.SDK_INT >= 14) {
            //不为systembar预留空间
            parentView.setFitsSystemWindows(false);
            //如果有虚拟键则在底部留出空间
            parentView.setPadding(0, 0, 0, DisplayUtility.getVirtualKeyHeight(this));
            parentView.setBackgroundColor(Color.BLACK);
        }
    }


}
