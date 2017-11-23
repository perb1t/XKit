package com.shijiwei.xkit.widget.chart;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by shijiwei on 2016/9/22.
 */
public class LineChart extends View {

    private static final String TAG = "LineChart";

    private List<Integer> mValueSet;

    private int[] mColors = {
            Color.argb(255, 255, 00, 00),
            Color.argb(255, 00, 00, 00),
    };

    private Paint mPain;

    private int mWidth;
    private int mHeight;

    private int mMaxValue;
    private int mMinValue;
    private int mOffset;

    private float mCircleRadius = 3;
    private float mValueTextSize = 12;
    private float mLastValueLength;
    private float mPendingTopAndBottom;
    private float mPendingLeftAndRight;
    private float mPending = 10;

    public LineChart(Context context) {
        this(context, null);
    }

    public LineChart(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LineChart(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context);
    }

    private void initialize(Context context) {

        mValueSet = new ArrayList<>();

        mPain = new Paint();
        mPain.setAntiAlias(true);
        mPain.setStrokeCap(Paint.Cap.ROUND);
        mPain.setStrokeWidth(1);
        mPain.setTextSize(mValueTextSize);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        mWidth = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        mHeight = getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec);

        //        mPendingTopAndBottom = mHeight / 10;
        //        mPendingLeftAndRight = mWidth / 10;

        mPendingLeftAndRight = mCircleRadius + mLastValueLength * mValueTextSize / 2 + mPending;
        mPendingTopAndBottom = mCircleRadius + mValueTextSize + mPending + mCircleRadius;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mValueSet != null && mValueSet.size() != 0) {

            float perX = (mWidth - mPendingLeftAndRight * 1) /
                    ((mValueSet.size() - 1) == 0 ? 1 : (mValueSet.size() - 1));

            float perY = (mHeight - mPendingTopAndBottom * 1) / (float) (mOffset == 0 ? 1 : mOffset);

            Path linePath = new Path();

            for (int i = 0; i < mValueSet.size(); i++) {

                if (i == 0) {
                    linePath.moveTo(
                            i * perX == 0 ? mCircleRadius + mPending : i * perX + mPending,
                            (mPendingTopAndBottom - mCircleRadius) + perY * (mMaxValue - mValueSet.get(i)));
                }

                linePath.lineTo(
                        i * perX == 0 ? mCircleRadius + mPending : i * perX + mPending,
                        (mPendingTopAndBottom - mCircleRadius) + perY * (mMaxValue - mValueSet.get(i)));
            }

            mPain.setStyle(Paint.Style.STROKE);
            mPain.setColor(mColors[1]);
            canvas.drawPath(linePath, mPain);

            mPain.setStyle(Paint.Style.FILL);
            mPain.setColor(mColors[0]);

            for (int i = 0; i < mValueSet.size(); i++) {

                canvas.drawCircle(
                        i * perX == 0 ? mCircleRadius + mPending : i * perX + mPending,
                        (mPendingTopAndBottom - mCircleRadius) + perY * (mMaxValue - mValueSet.get(i)),
                        mCircleRadius,
                        mPain);

                canvas.drawText("" + mValueSet.get(i),
                        i * perX == 0 ? mCircleRadius + mPending : i * perX + mPending,
                        (mPendingTopAndBottom - mCircleRadius) + perY * (mMaxValue - mValueSet.get(i)) - mCircleRadius - 7,
                        mPain);

            }


        }


    }

    /**
     * set a set of integer
     * @param valueSet
     */
    public void setValueSet(List<Integer> valueSet) {

        try {

            this.mValueSet = valueSet;

            List<Integer> tempSet = new ArrayList<>();
            tempSet.addAll(mValueSet);

            Collections.sort(tempSet, new Comparator<Integer>() {
                @Override
                public int compare(Integer lhs, Integer rhs) {
                    return lhs.compareTo(rhs);
                }
            });

            if (mValueSet != null) {

                if (mValueSet.size() != 0) {

                    mMaxValue = tempSet.get(tempSet.size() - 1);
                    mMinValue = tempSet.get(0);

                    mOffset = mMaxValue - mMinValue;

                    mLastValueLength = String.valueOf(this.mValueSet.get(mValueSet.size() - 1)).length();

                    invalidate();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();

            Log.e(TAG, "temperatureSet is null ");
        }


    }

    /**
     * set the color of the line
     * @param color
     */
    public void setLineColor(int color) {

        this.mColors[1] = color;
    }

    /**
     * set the color of the circle
     * @param color
     */
    public void setCircleColor(int color) {

        this.mColors[0] = color;
    }

    /**
     * set the radius of the circle
     * @param radius
     */
    public void setCircleRadius(int radius) {

        this.mCircleRadius = radius;

        this.mPendingTopAndBottom = mCircleRadius + mValueTextSize + mPending;
        this.mPendingLeftAndRight = mCircleRadius + mLastValueLength * mValueTextSize / 2 + mPending;

    }


    /**
     * dp2px
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * px2dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 根据设备信息获取当前分辨率下指定单位对应的像素大小；
     * px,dip,sp -> px
     */
    public float getRawSize(Context c, int unit, float size) {
        Resources r;
        if (c == null) {
            r = Resources.getSystem();
        } else {
            r = c.getResources();
        }
        return TypedValue.applyDimension(unit, size, r.getDisplayMetrics());
    }
}
