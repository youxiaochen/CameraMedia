package you.chen.media.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.TextureView;
import android.view.View;
import android.widget.TextView;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import androidx.appcompat.app.AppCompatActivity;
import you.chen.media.R;
import you.chen.media.camera.CameraHelper;
import you.chen.media.camera.CameraUtils;
import you.chen.media.camera.OrientationHelper;
import you.chen.media.camera.SizeFilter;
import you.chen.media.camera.impl.VideoSizeFilter;
import you.chen.media.core.Constant;
import you.chen.media.core.mp4.Mp4Recorder;
import you.chen.media.utils.FileUtils;
import you.chen.media.utils.LogUtils;
import you.chen.media.utils.Utils;
import you.chen.media.widget.CameraView;
import you.chen.media.widget.FocusView;

/**
 * Created by you on 2018-05-06.
 */
public class Mp4Activity extends AppCompatActivity
        implements View.OnClickListener, TextureView.SurfaceTextureListener {

    CameraHelper helper;
    //surface
    CameraView cv_camera;
    //聚焦动画控件
    FocusView fv_focus;
    //camera最佳筛选
    SizeFilter filter;
    //预览的缩放相关参数
    Matrix matrix;

    Mp4Recorder recorder;
    //线程池执行
    ExecutorService service;

    TextView bt;
    //方向传感
    OrientationHelper orientationHelper;
    //后,前置摄像头
    int cameraId = Camera.CameraInfo.CAMERA_FACING_BACK;

    public static void lanuch(Context context) {
        context.startActivity(new Intent(context, Mp4Activity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_mp4);

        helper = new CameraHelper();
        filter = new VideoSizeFilter();
        service = Executors.newFixedThreadPool(3);
        orientationHelper = new OrientationHelper(Utils.context());
        helper.setPreviewCallback((data, camera) -> {
            if (recorder != null) {
                recorder.pushCameraDatas(data);
            }
        });

        initView();
    }

    private void initView() {
        cv_camera = findViewById(R.id.cv_camera);
        fv_focus = findViewById(R.id.fv_focus);
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

        bt = findViewById(R.id.bt);
        bt.setOnClickListener(this);
        if (CameraHelper.isSupportFrontCamera()) {
            findViewById(R.id.bt1).setVisibility(View.VISIBLE);
            findViewById(R.id.bt1).setOnClickListener(this);
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
        helper.closeCamera();
        stopRecording();
        super.onPause();
    }

    //开启相机
    private void startCamera() {
        matrix = helper.openVideoCamera(cv_camera.getSurfaceTexture(), cameraId, cv_camera.getWidth(),
                cv_camera.getHeight(), filter, Constant.DEF_MIN_FPS, Constant.DEF_MAX_FPS);
        if (matrix != null) {
            cv_camera.setTransform(matrix);
        }
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
    protected void onDestroy() {
        service.shutdown();
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt:
                if (!bt.isSelected()) {//start
                    startRecording();
                } else {
                    stopRecording();
                }
                break;
            case R.id.bt1:
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

    //录制
    private void startRecording() {

        String path = FileUtils.getCacheDirPath() + "mp4record.mp4";


        int width = helper.getPreSize().width;
        int height = helper.getPreSize().height;
        int orientation = helper.getCameraRotation(orientationHelper.getOrientation());

        try {
            recorder = new Mp4Recorder(path, width, height, matrix, orientation);
            recorder.start(service);
        } catch (IOException e) {
            e.printStackTrace();
        }

        bt.setSelected(true);
        bt.setText("end");
    }

    //停止录制
    private void stopRecording() {
        if (recorder != null) {
            recorder.stop();
            recorder = null;
        }
        bt.setSelected(false);
        bt.setText("start");
    }

}
