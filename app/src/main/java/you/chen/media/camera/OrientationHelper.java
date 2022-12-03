package you.chen.media.camera;

import android.content.Context;
import android.view.OrientationEventListener;


/**
 * Created by you on 2018-03-21.
 * 只监听手机方向感应旋转 0, 90, 180, 270
 *
 * 可以在后面新的appcompat版本中结合Lifecycle  实现 LifecycleEventObserver, 使用时 getLifecycle().addObserver(OrientationHelper)即可
 * override fun onStateChanged(owner: LifecycleOwner, event: Lifecycle.Event) {
 *         if (event == Lifecycle.Event.ON_RESUME) {
 *             enable()
 *         } else if (event == Lifecycle.Event.ON_PAUSE) {
 *             disable()
 *         } else if (event == Lifecycle.Event.ON_DESTROY) {
 *             owner.lifecycle.removeObserver(this)
 *         }
 * }
 */
public class OrientationHelper {  //上面注释

    private int orientation = 0;

    private final OrientationListener listener;

    public OrientationHelper(Context context) {
        listener = new OrientationListener(context.getApplicationContext(), this);
    }

    /**
     * 开启方向感应
     */
    public final void enable() {
        if (listener.canDetectOrientation()) {
            listener.enable();
        } else {
            listener.disable();
        }
    }

    /**
     * 取消方向感应
     */
    public final void disable() {
        listener.disable();
    }

    /**
     * 获取当前方向感应
     * @return
     */
    public final int getOrientation() {
        return orientation;
    }

    /**
     * 旋转角度改变
     * @param orientation
     */
    public void onOrientationChanged(int orientation) {
        //nothing to override
    }

    static class OrientationListener extends OrientationEventListener {

        OrientationHelper helper;

        public OrientationListener(Context context, OrientationHelper helper) {
            super(context);
            this.helper = helper;
        }

        /**
         * 转换当前角度为 0, 90, 180, 270, 360即为0
         * @param orientation
         * @return
         */
        private int transformOrientation(int orientation) {
            int rotation = (orientation + 45) / 90 * 90;
            if (rotation >= 360) rotation = 0;
            return rotation;
        }

        @Override
        public void onOrientationChanged(int orientation) {
            if (orientation == OrientationEventListener.ORIENTATION_UNKNOWN) return;
            orientation = transformOrientation(orientation);
            if (helper.orientation != orientation) {
                helper.orientation = orientation;
                helper.onOrientationChanged(orientation);
            }
        }
    }

}
