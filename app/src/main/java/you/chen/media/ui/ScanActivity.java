package you.chen.media.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.view.TextureView;
import android.widget.Toast;

import com.google.zxing.Result;

import androidx.appcompat.app.AppCompatActivity;
import you.chen.media.R;
import you.chen.media.camera.CameraHelper;
import you.chen.media.camera.CameraUtils;
import you.chen.media.camera.SizeFilter;
import you.chen.media.camera.impl.PictureSizeFilter;
import you.chen.media.core.h264.AvcTransform;
import you.chen.media.core.h264.ClipAvcTransform;
import you.chen.media.core.Constant;
import you.chen.media.core.Orientation;
import you.chen.media.core.Transform;
import you.chen.media.core.scan.DecoderHandler;
import you.chen.media.core.scan2.DecoderHandler2;
import you.chen.media.utils.LogUtils;
import you.chen.media.widget.CameraView;

/**
 * Created by you on 2018-04-09.
 */
public class ScanActivity extends AppCompatActivity implements TextureView.SurfaceTextureListener, DecoderHandler2.DecoderCallback {

    CameraView cv_camera;

    CameraHelper helper;
    //camera最佳筛选
    SizeFilter filter;
    //预览的缩放相关参数
    Matrix matrix;
    //扫描解析处理
    DecoderHandler2 handler;

    public static void lanuch(Context context) {
        context.startActivity(new Intent(context, ScanActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_scan);
        helper = new CameraHelper();
        filter = new PictureSizeFilter();

        helper.setPreviewCallback((data, camera) -> {
            if (handler != null) {
                handler.push(data);
            }
        });

        initView();
    }

    private void initView() {
        cv_camera = findViewById(R.id.cv_camera);

        cv_camera.setOnCameraGestureListener(new CameraView.OnCameraGestureListener() {
            @Override
            public void onHandleZoom(float zoomScale) {
                helper.handleZoom(zoomScale);
            }

            @Override
            public void onHandleFocus(float x, float y, int w, int h) {
                helper.handleFocus(CameraUtils.reverseRotate(x, y, w, h, matrix));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (cv_camera.isAvailable()) {
            startCamera();
        } else {
            cv_camera.setSurfaceTextureListener(this);
        }
    }

    @Override
    protected void onPause() {
        stopCamera();
        super.onPause();
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture texture, int width, int height) {
        startCamera();
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture texture, int width, int height) {
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture texture) {
        return true;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture texture) {
    }

    @Override
    public void handleResult(Result result) {
        if (isFinishing()) return;
        LogUtils.i("handlerResult " + result.getText());
        Toast.makeText(this, result.getText(), Toast.LENGTH_LONG).show();
    }

    private void startCamera() {
        matrix = helper.openScanCamera(cv_camera.getSurfaceTexture(), cv_camera.getWidth(), cv_camera.getHeight(),
                filter, Constant.DEF_MIN_FPS, Constant.DEF_MAX_FPS);

        if (matrix != null) {
            cv_camera.setTransform(matrix);
        }
        cv_camera.setMaxScale(helper.getMaxZoomScale());

        int w = helper.getPreSize().width;
        int h = helper.getPreSize().height;
        Point matrixSize = CameraUtils.matrixSize(w, h, matrix);

        Transform transform = matrixSize.equals(w, h) ?
                new AvcTransform(w, h, 0, Orientation.ROTATE90)
                : new ClipAvcTransform(matrixSize.x, matrixSize.y, w, h, 0, Orientation.ROTATE90);
        //Camera旋转90, 270时, w, h调换
        handler = new DecoderHandler2(matrixSize.y, matrixSize.x, transform, this);
    }

    //释放相机
    private void stopCamera() {
        helper.closeCamera();
        if (handler != null) {
            handler.stop();
            handler = null;
        }
        matrix = null;
    }

}
