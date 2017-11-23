package com.shijiwei.xkit.sample;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.shijiwei.xkit.R;
import com.shijiwei.xkit.app.base.BaseActivity;
import com.shijiwei.xkit.nohttp.base.SimpleRequest;
import com.shijiwei.xkit.widget.parallax.ParallaxListView;
import com.shijiwei.xkit.zxing.activity.MipcaActivityCapture;
import com.yanzhenjie.nohttp.RequestMethod;

public class MainActivity extends BaseActivity {

    public static final String SERVER = "http://api.nohttp.net/";
    public static final String URL_NOHTTP_JSONOBJECT = SERVER + "jsonObject";

    private ParallaxListView parallaxListView;
    private ArrayAdapter<String> mArrayAdapter;


    @Override
    public int getLayoutResId() {
        return R.layout.activity_main;
    }

    @Override
    public void initialData(Bundle savedInstanceState) {
        SimpleRequest<String> request = new SimpleRequest<String>(URL_NOHTTP_JSONOBJECT, RequestMethod.GET, String.class);
        request.add("name", "yanzhenjie") // String型。
                .add("pwd", 123) // int型。
                .add("userAge", 1.25) // double型。
                .add("nooxxx", 1.2F) // flocat型。
                // 请求头，是否要添加头，添加什么头，要看开发者服务器端的要求。
                .addHeader("Author", "sample")
                .setHeader("User", "Jason")
                // 设置一个tag, 在请求完(失败/成功)时原封不动返回; 多数情况下不需要。
                .setTag(new Object())
                // 设置取消标志。
                .setCancelSign(new Object());
        addTask2Queue(100, request);
    }

    @Override
    public void initialView() {
        parallaxListView = (ParallaxListView) findViewById(R.id.parallax_list_view);
        parallaxListView.setHeaderParallaxViewResource(R.mipmap.dogavator);
        mArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        parallaxListView.setAdapter(mArrayAdapter);
        char letter = 'A';
        for (int i = 0; i < 26; i++)
            mArrayAdapter.add("—————————— " + (char) (letter + i));
        mArrayAdapter.notifyDataSetChanged();



    }

    @Override
    public void initialEvn() {

    }

    @Override
    public void onBackKeyPressed() {

    }

    @Override
    public void onNetworkStateChanged(int type, boolean isAvailable) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("===== result ", data.getStringExtra(MipcaActivityCapture.KEY_STR_RESULT));
    }


}
