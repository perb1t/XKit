package com.shijiwei.xkit.widget.parallax;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

/**
 * Created by shijiwei on 2016/9/10.
 *
 * @VERSION 1.0
 */
public class ParallaxListView extends ListView {

    private static final String TAG = "ParallaxListView";

    private float zoomParallaxMultiplier = 1f;
    private long bounceDurationMillis = 2000;

    private boolean isInitialize = false;

    private int mWidth;
    private int mHeight;

    private int mMaxParallaxViewSize;
    private int mDefaultParallaxViewSize;

    private ImageView mParallaxImageView;

    private OnRefreshListener mOnRefreshListener;

    public ParallaxListView(Context context) {
        this(context, null);
    }

    public ParallaxListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ParallaxListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context);
    }

    private void initialize(Context context) {

        setVerticalScrollBarEnabled(false);
        setOverScrollMode(OVER_SCROLL_NEVER);

        //        View mHeaderView = LayoutInflater
        //                .from(context)
        //                .inflate(R.layout.layout_header_parallax_listview, null);

        //        mParallaxImageView = (ImageView) mHeaderView.findViewById(R.id.parallax_image);

        LinearLayout mHeaderView = new LinearLayout(context);

        AbsListView.LayoutParams lp = new AbsListView.LayoutParams(
                AbsListView.LayoutParams.MATCH_PARENT,
                AbsListView.LayoutParams.WRAP_CONTENT);
        mHeaderView.setLayoutParams(lp);

        mParallaxImageView = new ImageView(context);
        mParallaxImageView.setLayoutParams(lp);
        mParallaxImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

        mHeaderView.addView(mParallaxImageView);
        addHeaderView(mHeaderView);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        mWidth = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        mHeight = getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec);

        setMeasuredDimension(mWidth, mHeight);

        mDefaultParallaxViewSize = (int) (mHeight * 0.3);

        mMaxParallaxViewSize = (int) (mHeight * 0.5);

        if (!isInitialize) {

            mParallaxImageView.getLayoutParams().height = mDefaultParallaxViewSize;
            isInitialize = true;
        }

    }


    @Override
    protected boolean overScrollBy(int deltaX, int deltaY, int scrollX,
                                   int scrollY, int scrollRangeX,
                                   int scrollRangeY, int maxOverScrollX,
                                   int maxOverScrollY, boolean isTouchEvent) {

        if (mParallaxImageView != null && mParallaxImageView.getHeight() <= mMaxParallaxViewSize && isTouchEvent) {

            if (deltaY < 0) {

                int currentHeight = mParallaxImageView.getHeight();
                int refreshHeight = (int) (currentHeight - deltaY * zoomParallaxMultiplier);

                mParallaxImageView.getLayoutParams().height =
                        refreshHeight > mMaxParallaxViewSize ?
                                mMaxParallaxViewSize : refreshHeight;

                if (refreshHeight < mMaxParallaxViewSize)
                    mParallaxImageView.requestLayout();

            } else {

                int currentHeight = mParallaxImageView.getHeight();
                int refreshHeight = (int) (currentHeight - deltaY * zoomParallaxMultiplier);

                mParallaxImageView.getLayoutParams().height =
                        refreshHeight < mDefaultParallaxViewSize ?
                                mDefaultParallaxViewSize : refreshHeight;

                if (refreshHeight > mDefaultParallaxViewSize)
                    mParallaxImageView.requestLayout();

            }

        }

        return super.overScrollBy(deltaX, deltaY, scrollX, scrollY,
                scrollRangeX, scrollRangeY, maxOverScrollX,
                maxOverScrollY, isTouchEvent);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);

        if (mParallaxImageView != null) {

            View parent = (View) mParallaxImageView.getParent();

            if (mParallaxImageView.getHeight() > mDefaultParallaxViewSize) {

                mParallaxImageView.getLayoutParams().height = Math.max(mParallaxImageView.getHeight()
                        - (getPaddingTop() - parent.getTop()), mDefaultParallaxViewSize);

                parent.layout(parent.getLeft(), 0, parent.getRight(), parent.getHeight());

                mParallaxImageView.requestLayout();

            }
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent ev) {

        switch (ev.getAction()) {

            case MotionEvent.ACTION_UP:


                if (mParallaxImageView != null && mParallaxImageView.getHeight() > mDefaultParallaxViewSize) {

                    GoBackAnimation mGoBackAnimation =
                            new GoBackAnimation(mParallaxImageView, mDefaultParallaxViewSize);

                    mGoBackAnimation.setDuration(bounceDurationMillis);

                    mParallaxImageView.startAnimation(mGoBackAnimation);
                }

                break;

            case MotionEvent.ACTION_DOWN:
                mParallaxImageView.clearAnimation();
                break;
        }


        return super.onTouchEvent(ev);
    }

    /**
     * set the duration millis of the parallax view bounce to origin
     *
     * @param millis
     */
    public void setBounceDurationMillis(long millis) {
        this.bounceDurationMillis = millis;
    }

    /**
     * set the resource to the parallax imageview
     *
     * @param resId
     */
    public void setHeaderParallaxViewResource(int resId) {

        mParallaxImageView.setImageResource(resId);

    }

    /**
     * set the bitmap to the parallax imageview
     *
     * @param bm
     */
    public void setHeadParallaxViewBitmap(Bitmap bm) {

        mParallaxImageView.setImageBitmap(bm);
    }


    /**
     * the multiplier of the parallax effect
     * <p/>
     * Suggest that between 0.5f and 1.0f
     *
     * @param zoomParallaxMultiplier
     */
    public void setZoomParallaxMultiplier(float zoomParallaxMultiplier) {

        this.zoomParallaxMultiplier = zoomParallaxMultiplier;
    }

    /**
     * @param onRefreshListener
     */
    public void setOnRefreshListener(OnRefreshListener onRefreshListener) {

        this.mOnRefreshListener = onRefreshListener;
    }

    /**
     * notify the listview when network  completed
     */
    public void notifyRefreshComplete() {
        // TODO: 2016/9/26
    }

    /**
     * a custom animation for go back
     */
    private class GoBackAnimation extends Animation {

        private View mTargetView;

        private int mTargetHeight;

        public GoBackAnimation(View targetView, int targetHeight) {

            this.mTargetHeight = targetHeight;
            this.mTargetView = targetView;
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {

            // 0.0f ~ 1.0f

            int currentHeight = (int) (mTargetHeight +
                    (mTargetView.getHeight() - mTargetHeight) * (1 - interpolatedTime));

            mTargetView.getLayoutParams().height = currentHeight;

            mTargetView.getParent().requestLayout();
            mTargetView.requestLayout();
        }
    }

    /**
     * a interface for pull to refresh
     */
    public interface OnRefreshListener {

        void onRefresh();

        void onLoadMore();
    }
}

