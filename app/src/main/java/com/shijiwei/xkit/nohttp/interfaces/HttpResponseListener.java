package com.shijiwei.xkit.nohttp.interfaces;

import com.yanzhenjie.nohttp.error.NetworkError;
import com.yanzhenjie.nohttp.error.ServerError;
import com.yanzhenjie.nohttp.error.TimeoutError;
import com.yanzhenjie.nohttp.rest.OnResponseListener;
import com.yanzhenjie.nohttp.rest.Response;
import com.yanzhenjie.nohttp.rest.RestResponse;

import java.net.ConnectException;

import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.SSLPeerUnverifiedException;

/**
 * Created by shijiwei on 2017/8/1.
 */
public class HttpResponseListener<Result> implements OnResponseListener<Result> {

    private RequestListener<Result> mActualRequestListener;

    public HttpResponseListener(RequestListener<Result> mActualRequestListener) {
        this.mActualRequestListener = mActualRequestListener;
    }

    @Override
    public void onStart(int what) {
        if (mActualRequestListener != null)
            mActualRequestListener.onHttpStart(what);
    }

    @Override
    public void onSucceed(int what, Response<Result> response) {

        if (mActualRequestListener == null) return;

        int responseCode = response.getHeaders().getResponseCode();
        if (responseCode == 200 || responseCode == 304){
            // 网络请求成功
            mActualRequestListener.onHttpSucceed(what,response);
        }else {
            // 网络请求异常，这里可以传一个你的自定义异常
            Response<Result> errorResponse = new RestResponse<>(response.request(),
                    response.isFromCache(),
                    response.getHeaders(),
                    response.get(),
                    response.getNetworkMillis(),
                    new ServerError("code = " + responseCode));
            onFailed(what, errorResponse);
        }
    }

    @Override
    public void onFailed(int what, Response<Result> response) {

        String errorMsg = "未知异常";
        Exception e = response.getException();

        if (e instanceof NetworkError){
            // 网络未连接
            errorMsg = "未连接网络";
        } else if (e instanceof TimeoutError){
            // 请求超时
            errorMsg = "网络超时";
        }else if (e instanceof ConnectException){
            // 连接失败
            errorMsg = "网络连接失败";
        }else if (e instanceof SSLPeerUnverifiedException
                || e instanceof SSLHandshakeException
                || e instanceof SSLException){
            // 证书异常
            errorMsg = "安全证书异常";
        }

        // 自定义异常。
        Response<Result> errorResponse = new RestResponse<>(response.request(),
                response.isFromCache(),
                response.getHeaders(),
                response.get(),
                response.getNetworkMillis(),
                new ServerError(errorMsg));

        if (mActualRequestListener != null)
            mActualRequestListener.onHttpFailed(what,errorResponse);
    }

    @Override
    public void onFinish(int what) {
        if (mActualRequestListener != null)
            mActualRequestListener.onHttpFinish(what);
    }
}
