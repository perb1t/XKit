package com.shijiwei.xkit.sample;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.shijiwei.xkit.R;
import com.shijiwei.xkit.utility.camera.CameraPreview;
import com.shijiwei.xkit.utility.log.LogUtil;

/**
 * Created by shijiwei on 2017/9/22.
 *
 * @VERSION 1.0
 */

public class XX extends Activity {

    private CameraPreview cameraPreview;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_camera);
        LogUtil.e("onCreate");

        cameraPreview = findViewById(R.id.camera);

    }

    @Override
    protected void onStart() {
        super.onStart();
        LogUtil.e("onStart");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        LogUtil.e("onRestart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        LogUtil.e("onResume" + cameraPreview == null ? " true" : " false");

    }

    @Override
    protected void onPause() {
        super.onPause();
        LogUtil.e("onPause");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtil.e("onDestroy");
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        LogUtil.e("onConfigurationChanged  " + getWindowManager().getDefaultDisplay()
                .getRotation());

        cameraPreview.setCameraDisplayOrientation(this);

    }
}
