package you.chen.media.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.TextureView;
import android.view.View;

import java.io.File;

import androidx.appcompat.app.AppCompatActivity;
import you.chen.media.R;
import you.chen.media.camera.CameraHelper;
import you.chen.media.camera.CameraUtils;
import you.chen.media.camera.OrientationHelper;
import you.chen.media.camera.SizeFilter;
import you.chen.media.camera.impl.PictureSizeFilter;
import you.chen.media.utils.BitmapUtils;
import you.chen.media.utils.FileUtils;
import you.chen.media.utils.LogUtils;
import you.chen.media.utils.Utils;
import you.chen.media.widget.CameraView;
import you.chen.media.widget.FlashView;
import you.chen.media.widget.FocusView;

/**
 * Created by you on 2018-01-08.
 *
 */
public class CameraActivity extends AppCompatActivity
        implements TextureView.SurfaceTextureListener, View.OnClickListener {

    CameraHelper helper;
    //camera最佳筛选
    SizeFilter filter;
    //预览的缩放相关参数
    Matrix matrix;

    //surface
    CameraView cv_camera;
    //聚焦动画控件
    FocusView fv_focus;
    //前后置相机切换
    View iv_switch;
    //闪光灯
    FlashView fv_flash;
    //方向传感
    OrientationHelper orientationHelper;
    //后,前置摄像头
    int cameraId = Camera.CameraInfo.CAMERA_FACING_BACK;

    public static void lanuch(Context context) {
        context.startActivity(new Intent(context, CameraActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_camera);

        helper = new CameraHelper();
        filter = new PictureSizeFilter();
        orientationHelper = new OrientationHelper(Utils.context()) {
            @Override
            public void onOrientationChanged(int orientation) {
                LogUtils.i("onOrientationChanged %d", orientation);
                helper.setOrientation(orientation);
            }
        };

        initView();
    }

    private void initView() {
        cv_camera = findViewById(R.id.cv_camera);
        fv_focus = findViewById(R.id.fv_focus);
        iv_switch = findViewById(R.id.iv_switch);
        fv_flash = findViewById(R.id.fv_flash);

        findViewById(R.id.bt).setOnClickListener(this);
        iv_switch.setOnClickListener(this);

        cv_camera.setOnCameraGestureListener(new CameraView.OnCameraGestureListener() {
            @Override
            public void onHandleZoom(float zoomScale) {
                helper.handleZoom(zoomScale);
                LogUtils.i("handlerZoom %f", zoomScale);
            }

            @Override
            public void onHandleFocus(float x, float y, int w, int h) {
                if (cameraId != Camera.CameraInfo.CAMERA_FACING_FRONT) {
                    helper.handleFocus(CameraUtils.reverseRotate(x, y, w, h, matrix));
                    fv_focus.setCenter(x, y);
                }
            }
        });

        fv_flash.setOnFlashChangedListener(model -> helper.setFlashMode(model));
        if (CameraHelper.isSupportFrontCamera()) {
            iv_switch.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        orientationHelper.enable();
        if (cv_camera.isAvailable()) {
            startCamera();
        } else {
            cv_camera.setSurfaceTextureListener(this);
        }
    }

    @Override
    protected void onPause() {
        orientationHelper.disable();
        stopCamera();
        super.onPause();
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        startCamera();
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return true;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {//nothing
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt:
                helper.takePicture((data, camera) -> {
                    Bitmap bitmap = CameraUtils.bytesToBitmap(data, matrix, helper.getOrientation());
                    File f = new File(FileUtils.getCacheDirPath(), "123.png");
                    BitmapUtils.saveBitmap(bitmap, f, true);
                });
                break;
            case R.id.iv_switch:
                if (cameraId == Camera.CameraInfo.CAMERA_FACING_BACK) {
                    cameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;
                } else {
                    cameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
                }
                helper.closeCamera();
                matrix = null;
                startCamera();
                break;
        }
    }

    //开启相机
    private void startCamera() {
        matrix = helper.openPicCamera(cv_camera.getSurfaceTexture(), cameraId,
                cv_camera.getWidth(), cv_camera.getHeight(), filter, orientationHelper.getOrientation());

        if (matrix != null) {
            cv_camera.setTransform(matrix);
        }
        cv_camera.setMaxScale(helper.getMaxZoomScale());
        fv_flash.setFlashModes(helper.getSupportedFlashModes(), cameraId == Camera.CameraInfo.CAMERA_FACING_FRONT);
    }

    //释放相机
    private void stopCamera() {
        helper.closeCamera();
        matrix = null;
    }

}
