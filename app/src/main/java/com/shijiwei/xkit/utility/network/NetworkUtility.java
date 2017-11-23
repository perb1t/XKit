package com.shijiwei.xkit.utility.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

/**
 * Created by shijiwei on 2017/5/9.
 */
public class NetworkUtility extends BroadcastReceiver implements NetworkHolder {

    /**
     * 未知网络
     */
    public static final int TYPE_UNKNOW = -2;
    /**
     * 暂未连接网络
     */
    public static final int TYPE_NONE = -1;
    /**
     * 移动网络
     */
    public static final int TYPE_MOBILE = 0;
    /**
     * WIFI-无线网络
     */
    public static final int TYPE_WIFI = 1;
    /**
     * 移动2G网络
     */
    public static final int TYPE_2G = 2;
    /**
     * 移动3G网络
     */
    public static final int TYPE_3G = 3;
    /**
     * 移动4G网络
     */
    public static final int TYPE_4G = 4;

    /**
     * 网络是否可以
     */
    private boolean enable = false;
    private boolean first = true;

    private ConnectivityManager mConnectivityManager;
    private TelephonyManager mTelephonyManager;
    private OnNetworkChangedListener onNetworkChangedListener;


    public NetworkUtility(Context context, OnNetworkChangedListener onNetworkChangedListener) {
        registerReceiver(context);
        this.onNetworkChangedListener = onNetworkChangedListener;
        excCheckNetworkState(context);
    }


    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
            if (mConnectivityManager == null || mTelephonyManager == null) {
                mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                mTelephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            }
            NetworkInfo activeNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
                onNetworkEvent(activeNetworkInfo);
            } else {
                onNetworkEvent(null);
            }
        }

    }

    @Override
    public void onNetworkEvent(NetworkInfo networkInfo) {
        if (networkInfo != null) {
            // network is available
            if (first || (!first && enable != networkInfo.isAvailable())) {

                switch (networkInfo.getType()) {
                    case TYPE_MOBILE:
                        if (onNetworkChangedListener != null)
                            onNetworkChangedListener.onNetworkChanged(getMobileNetworkType(mTelephonyManager.getNetworkType()), networkInfo.isAvailable());
                        break;
                    case TYPE_WIFI:
                        if (onNetworkChangedListener != null)
                            onNetworkChangedListener.onNetworkChanged(networkInfo.getType(), networkInfo.isAvailable());
                        break;
                }

                enable = networkInfo.isAvailable();
            }

        } else {
            // network unavailable
            if (first || (!first && enable)){
                if (onNetworkChangedListener != null)
                    onNetworkChangedListener.onNetworkChanged(TYPE_NONE, false);
                enable = false;
            }
        }

        if (first) first = false;
    }

    /**
     * 判断移动网络的类型
     *
     * @param networkType
     * @return
     */
    private int getMobileNetworkType(int networkType) {
        switch (networkType) {
            case TelephonyManager.NETWORK_TYPE_GPRS:
            case TelephonyManager.NETWORK_TYPE_EDGE:
            case TelephonyManager.NETWORK_TYPE_CDMA:
            case TelephonyManager.NETWORK_TYPE_1xRTT:
            case TelephonyManager.NETWORK_TYPE_IDEN:
                return TYPE_2G;
            case TelephonyManager.NETWORK_TYPE_UMTS:
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
            case TelephonyManager.NETWORK_TYPE_HSDPA:
            case TelephonyManager.NETWORK_TYPE_HSUPA:
            case TelephonyManager.NETWORK_TYPE_HSPA:
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
            case TelephonyManager.NETWORK_TYPE_EHRPD:
            case TelephonyManager.NETWORK_TYPE_HSPAP:
                return TYPE_3G;
            case TelephonyManager.NETWORK_TYPE_LTE:
                return TYPE_4G;
            default:
                return TYPE_UNKNOW;
        }
    }

    public void registerReceiver(Context context) {
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        context.registerReceiver(this, filter);
    }

    public void unregisterReceiver(Context context) {
        context.unregisterReceiver(this);
    }

    /**
     * 主动检测网络状态
     *
     * @param context
     */
    public void excCheckNetworkState(Context context) {
        onReceive(context, new Intent(ConnectivityManager.CONNECTIVITY_ACTION));
    }


    /**
     * 设备网络切换监听回调
     */
    public interface OnNetworkChangedListener {
        void onNetworkChanged(int type, boolean isAvailable);
    }

}
