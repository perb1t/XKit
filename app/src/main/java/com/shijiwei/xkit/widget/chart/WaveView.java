package com.shijiwei.xkit.widget.chart;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by shijiwei on 2016/9/8.
 */
public class WaveView extends View {
    private Paint mPaint;
    private int mWidth;
    private int mHeight;
    private Path mPath;
    private static final int NEED_INVALIDATE = 0X007;
    private int count = 0;
    private int size = 0;
    private boolean isAdd = true;

    private int perWaveWidth;
    private final int MAX_WAVE_HEIGHT = 100;
    private int decibel; // 分贝值

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case NEED_INVALIDATE:

                    if (isAdd) {

                        size++;

                        if (size > 20)
                            isAdd = false;

                    } else {

                        size--;

                        if (size < -21)
                            isAdd = true;

                    }

                    invalidate();

                    handler.sendEmptyMessageDelayed(NEED_INVALIDATE, 20);

                    break;
            }
        }
    };

    public WaveView(Context context, AttributeSet attrs) {
        super(context, attrs);

        initialWave();

    }

    public WaveView(Context context) {
        super(context);

        initialWave();
    }

    private void initialWave() {

        mPaint = new Paint();
        mPaint.setColor(Color.BLUE);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(1);
        mPaint.setTextSize(50);
        mPaint.setAntiAlias(true);
        mPath = new Path();

        setDecibel(100);
        handler.sendEmptyMessageDelayed(NEED_INVALIDATE, 100);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        mWidth = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        mHeight = getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec);

        perWaveWidth = mWidth / 21;

        setMeasuredDimension(mWidth, mHeight);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        mPath.reset();

        mPath.moveTo(0, mHeight / 2);

        int rate = 0;

        boolean isIncrease = true;

        for (int i = 1; i <= 21; i++) {

            if (isIncrease) {
                rate++;
                if (rate == 11)
                    isIncrease = false;
            } else {
                rate--;
            }


            if (i % 2 == 0)
                mPath.rQuadTo(perWaveWidth / 2, (size + decibel) / 10 * rate, perWaveWidth, 0);
            else
                mPath.rQuadTo(perWaveWidth / 2, -(size + decibel) / 10 * rate, perWaveWidth, 0);

        }

        canvas.drawPath(mPath, mPaint);

    }


    //设置音量
    public void setDecibel(int decibel) {

        this.decibel = decibel;
    }
}
