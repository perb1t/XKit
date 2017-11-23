package com.shijiwei.xkit.utility.camera;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

import com.shijiwei.xkit.utility.log.LogUtil;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * Created by shijiwei on 2017/4/7.
 */
public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback, Camera.PictureCallback, AspectRatioViewInterface, Camera.AutoFocusCallback {

    private static final String TAG = "CameraPreview";

    private Camera mHardwareCamera;
    private Camera.Parameters mCameraParameters;
    private CameraPreviewCallBack mCameraPreviewCallBack;

    private int mCameraId = 1;

    private double mRequestedAspect = -1.0;

    public static final int RESULT_CODE_OPEN_ERROR = 1; // opne camera failed

    /**
     * 是否打开预览
     */
    private boolean isPreviewing = false;

    private Bitmap mPicture;
    private Handler mCompressHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (mCameraPreviewCallBack != null)
                mCameraPreviewCallBack.onPictureTaken(mPicture);
            mHardwareCamera.startPreview();
            isPreviewing = true;
            return false;
        }
    });

    public CameraPreview(Context context) {
        this(context, null);
    }

    public CameraPreview(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CameraPreview(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initalize();
    }

    private void initalize() {
        //下面设置surfaceView不维护自己的缓冲区,而是等待屏幕的渲染引擎将内容推送到用户面前
        getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        getHolder().addCallback(this);
    }


    @Override
    public void setAspectRatio(double aspectRatio) {
        if (aspectRatio < 0) {
            throw new IllegalArgumentException();
        }
        if (mRequestedAspect != aspectRatio) {
            mRequestedAspect = aspectRatio;
            requestLayout();
        }
    }

    @Override
    public void onPause() {

    }

    @Override
    public void onResume() {

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        if (mRequestedAspect > 0) {
            int initialWidth = MeasureSpec.getSize(widthMeasureSpec);
            int initialHeight = MeasureSpec.getSize(heightMeasureSpec);

            final int horizPadding = getPaddingLeft() + getPaddingRight();
            final int vertPadding = getPaddingTop() + getPaddingBottom();
            initialWidth -= horizPadding;
            initialHeight -= vertPadding;

            final double viewAspectRatio = (double) initialWidth / initialHeight;
            final double aspectDiff = mRequestedAspect / viewAspectRatio - 1;

            if (Math.abs(aspectDiff) > 0.01) {
                if (aspectDiff > 0) {
                    // width priority decision
                    initialHeight = (int) (initialWidth / mRequestedAspect);
                } else {
                    // height priority decison
                    initialWidth = (int) (initialHeight * mRequestedAspect);
                }
                initialWidth += horizPadding;
                initialHeight += vertPadding;
                widthMeasureSpec = MeasureSpec.makeMeasureSpec(initialWidth, MeasureSpec.EXACTLY);
                heightMeasureSpec = MeasureSpec.makeMeasureSpec(initialHeight, MeasureSpec.EXACTLY);
            }
        }

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        try {
            ((Activity) getContext()).getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            //1代表前置摄像头，0代表后置摄像头
            mHardwareCamera = Camera.open(mCameraId);
            setCameraDisplayOrientation((Activity) getContext());
            mHardwareCamera.setPreviewDisplay(holder);
            //开始预览
            mHardwareCamera.startPreview();
            //自动对焦
            mHardwareCamera.autoFocus(this);
            //设置是否预览参数为真
            isPreviewing = true;
        } catch (IOException e) {
            mCameraPreviewCallBack.onCameraPreviewError(RESULT_CODE_OPEN_ERROR);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        LogUtil.e(TAG + " - surfaceChanged  " + width + " , " + height);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (mHardwareCamera != null) {
            mHardwareCamera.setPreviewCallback(null);
            mHardwareCamera.stopPreview();
            isPreviewing = false;
            mHardwareCamera.release();
            mHardwareCamera = null;
        }
    }

    @Override
    public void onPictureTaken(byte[] data, Camera camera) {
        if (mHardwareCamera == null) return;
        mHardwareCamera.stopPreview();
        isPreviewing = false;
        mPicture = null;
        if (data == null) {
            mCompressHandler.sendEmptyMessage(0);
        } else {
            zoomPicture(data, 100);
        }
    }

    /**
     * 自动对焦回调
     *
     * @param b
     * @param camera
     */
    @Override
    public void onAutoFocus(boolean b, Camera camera) {

    }


    public interface CameraPreviewCallBack {

        void onPictureTaken(Bitmap picture);

        void onCameraPreviewError(int code);
    }

    public void setCameraPreviewCallBack(CameraPreviewCallBack cameraPreviewCallBack) {
        this.mCameraPreviewCallBack = cameraPreviewCallBack;
    }


    public void takePicture() {
        if (mHardwareCamera != null && isPreviewing)
            mHardwareCamera.takePicture(null, null, this);
    }


    /**
     * 图片压缩到指定大小 ,单位K
     *
     * @param data
     * @param size
     */
    private void zoomPicture(final byte[] data, final int size) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                BitmapFactory.Options opts = new BitmapFactory.Options();
                opts.inJustDecodeBounds = true;
                BitmapFactory.decodeByteArray(data, 0, data.length, opts);
                int height = opts.outHeight;
                int width = opts.outWidth;
                float h = 800f;
                float w = 480f;
                int inSampleSize = 1;
                if (height > h || width > w) {
                    int inSampleSizeH = (int) (height / h) + (height % h == 0 ? 0 : 1);
                    int inSampleSizeW = (int) (width / w) + (width % w == 0 ? 0 : 1);
                    inSampleSize = Math.max(inSampleSizeH, inSampleSizeW);
                }
                opts.inSampleSize = inSampleSize;
                opts.inJustDecodeBounds = false;

                Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length, opts);
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);

                float zoom = (float) Math.sqrt(size * 1024 / (float) bos.toByteArray().length);
                if (zoom < 1) {
                    mPicture = rotateBitmap2ZeroDegree(bitmap, 270);
                    mCompressHandler.sendEmptyMessage(0);
                    return;
                }
                Matrix matrix = new Matrix();
                matrix.setScale(zoom, zoom);
                Bitmap result = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                bos.reset();
                result.compress(Bitmap.CompressFormat.PNG, 85, bos);
                while (bos.toByteArray().length > size * 1024) {
                    matrix.setScale(0.9f, 0.9f);
                    result = Bitmap.createBitmap(result, 0, 0, result.getWidth(), result.getHeight(), matrix, true);
                    bos.reset();
                    result.compress(Bitmap.CompressFormat.PNG, 85, bos);
                }

                Bitmap bitmap2 = BitmapFactory.decodeStream(new ByteArrayInputStream(bos.toByteArray()));

                // 照片旋转到正常角度
                mPicture = rotateBitmap2ZeroDegree(bitmap2, 270);
                mCompressHandler.sendEmptyMessage(0);
            }
        }).start();
    }


    private Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int w, int h) {
        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio = (double) w / h;
        if (sizes == null) return null;

        Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        int targetHeight = h;

        // Try to find an size match aspect ratio and size
        for (Camera.Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        // Cannot find the one match the aspect ratio, ignore the requirement
        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        return optimalSize;
    }

    /**
     * 通过对比得到与宽高比最接近的预览尺寸（如果有相同尺寸，优先选择）
     *
     * @param isPortrait    是否竖屏
     * @param surfaceWidth  需要被进行对比的原宽
     * @param surfaceHeight 需要被进行对比的原高
     * @param preSizeList   需要对比的预览尺寸列表
     * @return 得到与原宽高比例最接近的尺寸
     */
    public static Camera.Size getCloselyPreSize(boolean isPortrait, int surfaceWidth, int surfaceHeight, List<Camera.Size> preSizeList) {
        int reqTmpWidth;
        int reqTmpHeight;
        // 当屏幕为垂直的时候需要把宽高值进行调换，保证宽大于高
        if (isPortrait) {
            reqTmpWidth = surfaceHeight;
            reqTmpHeight = surfaceWidth;
        } else {
            reqTmpWidth = surfaceWidth;
            reqTmpHeight = surfaceHeight;
        }
        //先查找preview中是否存在与surfaceview相同宽高的尺寸
        for (Camera.Size size : preSizeList) {
            if ((size.width == reqTmpWidth) && (size.height == reqTmpHeight)) {
                return size;
            }
        }

        // 得到与传入的宽高比最接近的size
        float reqRatio = ((float) reqTmpWidth) / reqTmpHeight;
        float curRatio, deltaRatio;
        float deltaRatioMin = Float.MAX_VALUE;
        Camera.Size retSize = null;
        for (Camera.Size size : preSizeList) {
            curRatio = ((float) size.width) / size.height;
            deltaRatio = Math.abs(reqRatio - curRatio);
            if (deltaRatio < deltaRatioMin) {
                deltaRatioMin = deltaRatio;
                retSize = size;
            }
        }

        return retSize;
    }


    /**
     * 将图片旋转角度
     *
     * @param source
     * @param degree
     * @return
     */
    private Bitmap rotateBitmap2ZeroDegree(Bitmap source, int degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        return Bitmap.createBitmap(
                source,
                0, 0, source.getWidth(), source.getHeight(),
                matrix,
                true);
    }

    /**
     * 根据屏幕的走向设置摄像头的角度，在onConfigurationChanged（）方法中调用
     * activity 需要在 manifest 中添加 android:configChanges="orientation|screenSize|keyboardHidden">
     *
     * @param activity
     */
    public void setCameraDisplayOrientation(Activity activity) {

        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(mCameraId, info);
        int degrees = 0;
        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360; // compensate the mirror
        } else {
            // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        mHardwareCamera.setDisplayOrientation(result);
    }
}
