package you.chen.media.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.TextureView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import androidx.appcompat.app.AppCompatActivity;
import io.reactivex.disposables.Disposable;
import you.chen.media.R;
import you.chen.media.camera.CameraHelper;
import you.chen.media.camera.CameraUtils;
import you.chen.media.camera.OrientationHelper;
import you.chen.media.camera.SizeFilter;
import you.chen.media.camera.impl.VideoSizeFilter;
import you.chen.media.core.Constant;
import you.chen.media.core.MediaEncoder;
import you.chen.media.core.h264.H264Callback;
import you.chen.media.core.h264.H264Utils;
import you.chen.media.core.h264.H264MuxerCallback;
import you.chen.media.rx.RxUtils;
import you.chen.media.utils.FileUtils;
import you.chen.media.utils.Utils;
import you.chen.media.widget.CameraView;
import you.chen.media.widget.FocusView;

/**
 * Created by you on 2018-01-08.
 */
public class H264Activity extends AppCompatActivity
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

    MediaEncoder recorder;
    //线程池执行
    ExecutorService service;

    TextView bt;
    //方向传感
    OrientationHelper orientationHelper;
    //后,前置摄像头
    int cameraId = Camera.CameraInfo.CAMERA_FACING_BACK;

    CheckBox cb;

    public static void lanuch(Context context) {
        context.startActivity(new Intent(context, H264Activity.class));
    }

    //测试统计Camera的帧率
    int countRate = 0;
    Disposable c;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_h264);

        helper = new CameraHelper();
        filter = new VideoSizeFilter();
        service = Executors.newSingleThreadExecutor();

        helper.setPreviewCallback((data, camera) -> {
            if (recorder != null) {
                recorder.push(data);
            }
            countRate++;
        });
        orientationHelper = new OrientationHelper(Utils.context());

        c = RxUtils.sinterval(1).subscribe(aLong -> {
            //LogUtils.i("countRate %d", countRate);//打印统计Camera的帧率
            countRate = 0;
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
        cb = findViewById(R.id.cb_muxer);
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
        c.dispose();
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
                if (recorder != null) return;//正在录制时不许切换
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
        String saveName = cb.isChecked() ? "h264ByMuxer.h264" : "simple.h264";
        int width = helper.getPreSize().width;
        int height = helper.getPreSize().height;
        int orientation = helper.getCameraRotation(orientationHelper.getOrientation());

        try {
            String path = FileUtils.getCacheDirPath()  + saveName;
            MediaEncoder.Callback callback = cb.isChecked() ? new H264MuxerCallback(path) : new H264Callback(path);
            recorder = H264Utils.createH264MediaEncoder(width, height, matrix, orientation, callback);
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
