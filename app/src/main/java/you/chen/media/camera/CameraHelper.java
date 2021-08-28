package you.chen.media.camera;

import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import you.chen.media.utils.LogUtils;

/**
 * Created by you on 2018-03-18.
 * 拍照, 视频, H264, 扫描的Camera各版本之间的兼容操作, 并支持 手动聚焦, 缩放, 闪光
 * https://developer.android.google.cn/guide/topics/media/camera#metering-focus-areas,参考
 */
public final class CameraHelper implements Camera.PreviewCallback {

    /**
     * 是否支持相机
     * @return
     */
    public static boolean isSupportCamera() {
        return Camera.getNumberOfCameras() > 0;
    }

    public static boolean isSupportFrontCamera() {
        final int cameraCount = Camera.getNumberOfCameras();
        Camera.CameraInfo info = new Camera.CameraInfo();
        for (int i = 0; i < cameraCount; i++) {
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                return true;
            }
        }
        return false;
    }

    /**
     * camera
     */
    private Camera mCamera;
    //预览字节缓存
    private byte[] buffer;
    //预览回调,在此回调中处理数据
    private Camera.PreviewCallback callback;
    //预览大小
    private Camera.Size preSize;
    /**
     * 当前相机的相关信息
     */
    private Camera.CameraInfo cameraInfo;

    private int orientation = 0;

    public CameraHelper() {
        cameraInfo = new Camera.CameraInfo();
    }

    public void setPreviewCallback(Camera.PreviewCallback callback) {
        this.callback = callback;
    }

    public Camera.Size getPreSize() {
        return preSize;
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        if (callback != null) {
            callback.onPreviewFrame(data, camera);
        }
        camera.addCallbackBuffer(buffer);
    }

    public Matrix openPicCamera(SurfaceTexture texture, int cameraId, int w, int h,
                                SizeFilter filter, int orientation) {
        return openCamera(texture, cameraId, w, h, filter,
                Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE, orientation, -1, -1);
    }

    public Matrix openVideoCamera(SurfaceTexture texture, int cameraId, int w, int h,
                                  SizeFilter filter, int minFps, int maxFps) {
        return openCamera(texture, cameraId, w, h, filter,
                Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO, 0, minFps, maxFps);
    }

    public Matrix openScanCamera(SurfaceTexture texture, int w, int h,
                                 SizeFilter filter, int minFps, int maxFps) {
        return openCamera(texture, Camera.CameraInfo.CAMERA_FACING_BACK, w, h, filter,
                Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE, 0, minFps, maxFps);
    }

    /**
     *
     * @param texture
     * @param cameraId {@link Camera.CameraInfo#CAMERA_FACING_BACK}
     *                  or {@link Camera.CameraInfo#CAMERA_FACING_FRONT}
     * @param w
     * @param h
     * @param filter
     * @param focusMode 聚焦模式
     * @param orientation 手机传感方向 {@link OrientationHelper#getOrientation()}
     * @param minFps h264时的最小帧率, 不需要时可传-1
     * @param maxFps h264时的最大帧率, 不需要时可传-1
     * @return
     */
    public Matrix openCamera(SurfaceTexture texture, int cameraId, int w, int h,
                             SizeFilter filter, String focusMode, int orientation,
                             int minFps, int maxFps) {
        this.orientation = orientation;
        if (mCamera == null) {
            mCamera = Camera.open(cameraId);

            try {
                mCamera.setDisplayOrientation(90);//默认竖直拍照
                Camera.getCameraInfo(cameraId, cameraInfo);

                Camera.Parameters parameters = mCamera.getParameters();
                //设置聚焦类型
                List<String> focusModes = parameters.getSupportedFocusModes();
                if (focusMode != null && focusModes.contains(focusMode)) {
                    parameters.setFocusMode(focusMode);
                }
                //设置Pic, Pre尺寸, 视频时可以忽略Pic尺寸
                Camera.Size picSize = filter.findOptimalPicSize(parameters.getSupportedPictureSizes(), w, h);
                if (picSize != null) {//拍摄图片时不可null
                    LogUtils.i("Camera Parameters picSize %d - %d", picSize.width, picSize.height);
                    parameters.setPictureSize(picSize.width, picSize.height);
                    //设置生成图片的旋转角度,只对拍摄照片时有效
                    parameters.setRotation(getCameraRotation(orientation));
                    parameters.setPictureFormat(ImageFormat.JPEG);
                }
                preSize = filter.findOptimalPreSize(parameters.getSupportedPreviewSizes(), picSize, w, h);
                if (preSize == null) throw new IllegalArgumentException("There is no matching presize");
                LogUtils.i("Camera Parameters preSize %d - %d", preSize.width, preSize.height);
                parameters.setPreviewSize(preSize.width, preSize.height);
                parameters.setPreviewFormat(ImageFormat.NV21);
                //设置帧率
                if (minFps > 0 && maxFps > 0) {
                    initPreviewFpsRange(parameters, minFps, maxFps);
                }
                mCamera.setParameters(parameters);

                buffer = new byte[preSize.width * preSize.height * 3 / 2];
                mCamera.setPreviewTexture(texture);
                mCamera.startPreview();
                mCamera.setPreviewCallbackWithBuffer(this);
                mCamera.addCallbackBuffer(buffer);
                return transformSurface(w, h, preSize.height, preSize.width);
            } catch (Exception e) {
                LogUtils.e(e);
                closeCamera();
            }
        }
        return null;
    }

    /**
     * 获取当前相机设备对应的旋转角度
     * @param orientation {@link OrientationHelper#getOrientation()}
     * @return 0, 90, 180, 270
     */
    public int getCameraRotation(int orientation) {
        if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            return (cameraInfo.orientation - orientation + 360) % 360;
        } else {
            return (cameraInfo.orientation + orientation) % 360;
        }
    }

    /**
     * 支持的闪光效果, 在开启相机之后获取
     * @return
     */
    public List<String> getSupportedFlashModes() {
        if (mCamera != null) {
            return mCamera.getParameters().getSupportedFlashModes();
        }
        return null;
    }

    /**
     * 设置当前闪光效果
     * @param flashMode
     */
    public void setFlashMode(String flashMode) {
        if (mCamera != null) {
            Camera.Parameters parameters = mCamera.getParameters();
            if (flashMode.equals(parameters.getFlashMode())) return;
            parameters.setFlashMode(flashMode);
            mCamera.setParameters(parameters);
        }
    }

    /**
     * 设置当前旋转角度, 此设置只可针对拍摄的图片有效
     * @param orientation
     */
    public void setOrientation(int orientation) {
        if (mCamera != null) {
            this.orientation = orientation;
            Camera.Parameters parameters = mCamera.getParameters();
            parameters.setRotation(getCameraRotation(orientation));
            mCamera.setParameters(parameters);
        }
    }

    /**
     * Camera对应的屏幕旋转角度
     * @return
     */
    public int getOrientation() {
        return orientation;
    }

    /**
     * 释放摄像头
     */
    public void closeCamera() {
        buffer = null;
        preSize = null;
        if (mCamera != null) {
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }

    /**
     * 拍摄照片
     * @param callback
     */
    public void takePicture(Camera.PictureCallback callback) {
        if (mCamera != null) {
            mCamera.takePicture(null, null, callback);
        }
    }

    /**
     * 缩放
     * @param targetRatio 放大的比例
     */
    public void handleZoom(float targetRatio) {
        if (mCamera == null) return;
        Camera.Parameters parameters = mCamera.getParameters();
        if (!parameters.isZoomSupported()) return;
        List<Integer> zoomRatios = parameters.getZoomRatios();
        if (zoomRatios == null || zoomRatios.isEmpty()) return;
        int zoom = indexByBinary(zoomRatios, targetRatio * 100);
        if (zoom == parameters.getZoom()) return;
        parameters.setZoom(zoom);
        mCamera.setParameters(parameters);
    }

    /**
     * 手动聚焦
     */
    public void handleFocus(PointF scalePointF) {
        handleFocus(scalePointF, 200, 300);
    }

    /**
     * 手动聚焦
     * @param scalePointF 聚焦点坐标与聚焦点所预览的界面宽高的比例
     * @param fsize 聚焦方形区域大小
     * @param msize 测光方形区域大小
     */
    public void handleFocus(PointF scalePointF, int fsize, int msize) {
        if (mCamera == null) return;
        Camera.Parameters parameters = mCamera.getParameters();
        //一般使用能自动聚焦的即可
        String autoFocusMode = findFocusbackMode(parameters);
        if (autoFocusMode == null) return;
        mCamera.cancelAutoFocus();
        if (parameters.getMaxNumFocusAreas() > 0) {//聚焦区域
            List<Camera.Area> focusAreas = new ArrayList<>();
            //聚焦区域
            Rect focusRect = calculateTapArea(scalePointF, fsize);
            focusAreas.add(new Camera.Area(focusRect, 800));
            parameters.setFocusAreas(focusAreas);
        } else {
            LogUtils.i("focus areas not supported");
        }
        if (parameters.getMaxNumMeteringAreas() > 0) {//测光区域
            List<Camera.Area> meteringAreas = new ArrayList<>();
            Rect meteringRect = calculateTapArea(scalePointF, msize);
            meteringAreas.add(new Camera.Area(meteringRect, 800));
            parameters.setMeteringAreas(meteringAreas);
        } else {
            LogUtils.i("metering areas not supported");
        }

        final String currentFocusMode = parameters.getFocusMode();
        parameters.setFocusMode(autoFocusMode);
        mCamera.setParameters(parameters);
        mCamera.autoFocus((success, camera) -> {
            Camera.Parameters params = camera.getParameters();
            params.setFocusMode(currentFocusMode);
            camera.setParameters(params);
            //如果有设置自动对焦回调时不可设置为null
            camera.autoFocus(null);
            LogUtils.i("autoFocus..." + success);
        });
    }

    /**
     * 获取最大支持的缩放比例, 最小就为1.0f初始大小
     * @return
     */
    public float getMaxZoomScale() {
        if (mCamera != null) {
            Camera.Parameters parameters = mCamera.getParameters();
            if (parameters.isZoomSupported()) {
                //getZoomRatios源码The list is sorted from small to large
                List<Integer> zoomRatios = parameters.getZoomRatios();
                if (zoomRatios != null && zoomRatios.size() == parameters.getMaxZoom() + 1) {
                    int minZoom = zoomRatios.get(0);
                    float maxZoom = zoomRatios.get(zoomRatios.size() - 1);
                    return maxZoom / minZoom;
                }
            }
        }
        return 1.0f;
    }

    /**
     * 根据实际预览的尺寸来计算Surface的缩放与移动大小
     * 必须把Surface显示的与实现拍摄的预览界面的比较一致, 先调整比例,再调整偏移, 这样预览的效果即不会拉伸,拍出来的效果也与实际一致
     * @param sw surface的宽
     * @param sh surface的高
     * @param prew Camera.Size 预览宽,注意相机的旋转90
     * @param preh Camera.Size 预览高,注意相机的旋转90
     * @return 返回的Matrix中包含着X轴或者Y轴的缩放比例与偏移
     */
     private Matrix transformSurface(int sw, int sh, int prew, int preh) {
        Matrix matrix = new Matrix();
        float preScale = preh / (float) prew;
        float viewScale = sh / (float) sw;
        if (preScale != viewScale) {//宽高比例不一样,才需要做处理
            if (preScale > viewScale) {//将高宽比例较大的放到屏幕上显示, 所以需要截掉预览的一部分高, 即Y轴偏移
                //按预览的宽与需要显示的宽比例调整预览的高度
                float scalePreY = sw * preScale;// preHeight * (sWidth / preWidth);
                //Y轴需要放大的比例
                matrix.preScale(1.0f, scalePreY / sh);
                float translateY = (sh - scalePreY) / 2;
                matrix.postTranslate(0, translateY);
//                LogUtils.i("transY %f , %f", scalePreY, translateY);
            } else {//屏幕显示高宽尺寸比例较小的,即X轴偏移
                float scalePreX = sh / preScale; //preWidth * (sHeight / preHeight);
                //x轴需要放大的比例
                matrix.preScale(scalePreX / sw, 1.0f);
                float translateX = (sw - scalePreX) / 2;
                matrix.postTranslate(translateX, 0);
//                LogUtils.i("transX %f , %f", scalePreX, translateX);
            }
        }
        return matrix;
    }

    /**
     * 初始相机预览帧率, 一般相机都支持7K~30K, 可以设置15~25, 帧率过高时, 旋转裁剪低配手机编码速度容易跟不上
     * @param parameters
     * @param minFps 最小帧率 K
     * @param maxFps 最大帧率 K
     */
    private void initPreviewFpsRange(Camera.Parameters parameters, int minFps, int maxFps) {
        List<int[]> supportedPreviewFpsRange = parameters.getSupportedPreviewFpsRange();
        if (supportedPreviewFpsRange == null || supportedPreviewFpsRange.isEmpty()) return;
        int[] suitableFPSRange = null;
        for (int[] fpsRange : supportedPreviewFpsRange) {
//            LogUtils.i("supportPreviewFps %d - %d", fpsRange[0], fpsRange[1]);
            if (fpsRange[Camera.Parameters.PREVIEW_FPS_MIN_INDEX] >= minFps
                    && fpsRange[Camera.Parameters.PREVIEW_FPS_MAX_INDEX] <= maxFps) {
                suitableFPSRange = fpsRange;
                break;
            }
        }
        if (suitableFPSRange != null) {
            int[] currentFpsRange = new int[2];
            parameters.getPreviewFpsRange(currentFpsRange);
            if (Arrays.equals(currentFpsRange, suitableFPSRange)) {
                return;
            }
            parameters.setPreviewFpsRange(suitableFPSRange[Camera.Parameters.PREVIEW_FPS_MIN_INDEX],
                    suitableFPSRange[Camera.Parameters.PREVIEW_FPS_MAX_INDEX]);
        } else {
            LogUtils.i("No suitable FPS range in %d - %d", minFps, maxFps);
        }
    }

    /**
     * 焦点区域坐标点  (-1000, -1000, 1000, 1000),根据点坐标x,y轴与实际大小w,h比例计算出该点的区域大小
     * @param scalePointF 聚焦点坐标与聚焦点所预览的界面宽高的比例
     * @param areaSize 聚焦方形区域大小
     * @return
     */
    private Rect calculateTapArea(PointF scalePointF, int areaSize) {
        int centerX = (int) (scalePointF.x * 2000 - 1000);
        int centerY = (int) (scalePointF.y * 2000 - 1000);

        int left = clamp(centerX - areaSize / 2, -1000, 1000);
        int top = clamp(centerY - areaSize / 2, -1000, 1000);
        int right = clamp(left + areaSize, -1000, 1000);
        int bottom = clamp(top + areaSize, -1000, 1000);
        return new Rect(left, top, right, bottom);
    }

    /**
     * x值不能超出min~max范围
     */
    private int clamp(int x, int min, int max) {
        if (x > max) return max;
        if (x < min) return min;
        return x;
    }

    /**
     * 查找能回调出{@link Camera#autoFocus(Camera.AutoFocusCallback)}的聚焦模式
     * 源码注释中指出FOCUS_MODE_AUTO 与 FOCUS_MODE_MACRO 支持, 优先使用前者
     * @param parameters
     * @return
     */
    private String findFocusbackMode(Camera.Parameters parameters) {
        List<String> supportedFocusModes =  parameters.getSupportedFocusModes();
        if (supportedFocusModes != null && !supportedFocusModes.isEmpty()) {
            if (supportedFocusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) return Camera.Parameters.FOCUS_MODE_AUTO;
            if (supportedFocusModes.contains(Camera.Parameters.FOCUS_MODE_MACRO)) return Camera.Parameters.FOCUS_MODE_MACRO;
        }
        return null;
    }

    /**
     * 二分查找最接近的
     * @param ints
     * @param target
     * @return
     */
    private int indexByBinary(List<Integer> ints, float target) {
        int low = 0;
        int high = ints.size() - 1;
        if (ints.size() == 1) return 0;
        if (target <= ints.get(low)) return low;
        if (target >= ints.get(high)) return high;
        int middle = 0;
        float left, right;
        while(low <= high) {
            middle = (low + high)/2;
            right = Math.abs(ints.get(middle + 1) - target);
            left = Math.abs(ints.get(middle) - target);
            if(right > left) {
                high = middle - 1;
            } else {
                low = middle + 1;
            }
        }
        right = Math.abs(ints.get(middle + 1) - target);
        left = Math.abs(ints.get(middle) - target);
        return right > left ? middle : middle + 1;
    }

}
