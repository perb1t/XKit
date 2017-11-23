package com.shijiwei.xkit.nohttp.interfaces;

import com.yanzhenjie.nohttp.rest.Response;

/**
 * Created by shijiwei on 2017/8/1.
 */
public interface RequestListener<Result> {

    /**
     * When the request starts.
     * @param what the credit of the incoming request is used to distinguish between multiple requests.
     */
    void onHttpStart(int what);

    /**
     * Server correct response to callback when an HTTP request.
     * @param what         the credit of the incoming request is used to distinguish between multiple requests.
     * @param response     successful callback.
     */
    void onHttpSucceed(int what, Response<Result> response);

    /**
     * When there was an error correction.
     * @param what     the credit of the incoming request is used to distinguish between multiple requests.
     * @param response failure callback.
     */
    void onHttpFailed(int what, Response<Result> response);

    /**
     * When the request finish.
     * @param what the credit of the incoming request is used to distinguish between multiple requests.
     */
    void onHttpFinish(int what);
}
