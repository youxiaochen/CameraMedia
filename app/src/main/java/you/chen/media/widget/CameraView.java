package you.chen.media.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.TextureView;

/**
 * Created by you on 2018-03-23.
 */
public class CameraView extends TextureView {

    private GestureDetector gestureDetector;
    //手势监听
    private OnCameraGestureListener listener;

    private float currentScale = 1.0f;

    private float maxScale = Float.MAX_VALUE;

    public CameraView(Context context) {
        super(context);
        init(context);
    }

    public CameraView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CameraView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                return true;
            }

            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                if (listener != null) {
                    listener.onHandleFocus(e.getX(), e.getY(), getWidth(), getHeight());
                }
                return super.onSingleTapConfirmed(e);
            }
        });

        scaleGestureDetector = new ScaleGestureDetector(context, new ScaleGestureDetector.SimpleOnScaleGestureListener() {
            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                currentScale *= detector.getScaleFactor();
                if (currentScale < 1.f) currentScale = 1.f;
                if (currentScale > maxScale) currentScale = maxScale;
                if (listener != null) {
                    listener.onHandleZoom(currentScale);
                }
                return true;
            }
        });
    }

    private ScaleGestureDetector scaleGestureDetector;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean res = scaleGestureDetector.onTouchEvent(event);
        if (!scaleGestureDetector.isInProgress()) {
            return gestureDetector.onTouchEvent(event);
        }
        return res;
    }

    public void setOnCameraGestureListener(OnCameraGestureListener listener) {
        this.listener = listener;
    }

    /**
     * 设置支持的最大比例
     * @param maxScale
     */
    public final void setMaxScale(float maxScale) {
        this.maxScale = maxScale;
    }

    /**
     * 相机手势操作, 点击聚集与缩放预览
     */
    public interface OnCameraGestureListener {
        /**
         * 缩放手势
         * @param zoomScale 放大比例
         */
        void onHandleZoom(float zoomScale);

        /**
         * 聚集坐标
         * @param x
         * @param y
         * @param w
         * @param h
         */
        void onHandleFocus(float x, float y, int w, int h);
    }
}
