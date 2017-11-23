package com.shijiwei.xkit.utility.camera;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.FaceDetector;

/**
 * Created by shijiwei on 2017/2/27.
 */
public class FaceDetecorWrapper {

    /* 最多检测人脸数 */
    private static final int MAX_FACES = 10;
    /* 图片的最小边长 */
    private static final int MIN_WIDTH = 300;

    private FaceDetectCallback mFaceDetectCallback;
    private FaceDetector mFaceDetector;
    private FaceDetector.Face[] mFaceSet;
    private int mReallyDetectFaceNumber;

    public FaceDetecorWrapper() {
    }

    /**
     * 人脸检测监听
     */
    public interface FaceDetectCallback {
        /**
         * 返回检测人脸数
         *
         * @param faceNumber
         */
        void detectCallback(int faceNumber, Bitmap face);
    }

    /**
     * 检测图片上的人脸
     *
     * @param source
     */
    private void detect(Bitmap source) {
        mFaceSet = new FaceDetector.Face[MAX_FACES];
        Bitmap mFaceBitmap = copyToDemandBitmap(source);
        source.recycle();
        mFaceDetector = new FaceDetector(
                mFaceBitmap.getWidth(),
                mFaceBitmap.getHeight(),
                MAX_FACES);
        mReallyDetectFaceNumber = mFaceDetector.findFaces(mFaceBitmap, mFaceSet);
        if (mFaceDetectCallback != null)
            mFaceDetectCallback.detectCallback(mReallyDetectFaceNumber, mFaceBitmap);
    }

    /**
     * 将图片转换成检测所要求的RGB_565格式
     *
     * @param source
     * @return
     */
    private Bitmap copyToDemandBitmap(Bitmap source) {
        Matrix matrix = new Matrix();
        int width = source.getWidth();
        int height = source.getHeight();
        if (width <= MIN_WIDTH && height <= MIN_WIDTH) {
            return source.copy(Bitmap.Config.RGB_565, true);
        }
        float scale = Math.max((float) MIN_WIDTH / width, (float) MIN_WIDTH / height);
        matrix.postScale(scale, scale);
        return Bitmap
                .createBitmap(source, 0, 0, width, height, matrix, true)
                .copy(Bitmap.Config.RGB_565, true)
                ;
    }

    /**
     * 设置回调接口
     *
     * @param faceDetectCallback
     */
    public void setFaceDetectCallback(FaceDetectCallback faceDetectCallback) {
        this.mFaceDetectCallback = faceDetectCallback;
    }

    /**
     * 开始检测
     */
    public void start(Bitmap bitmap) {
        detect(bitmap);
    }
}
