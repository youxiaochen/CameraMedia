package you.chen.media.utils;

/**
 * Created by you on 2018/1/15.
 */

public final class ViewUtils {

    private ViewUtils() {}

    /**
     * dp 转 px
     * @param dpValue
     * @return
     */
    public static int dp2px(float dpValue) {
        final float scale = Utils.context().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * px 转 dp
     * @param pxValue
     * @return
     */
    public static int px2dp(float pxValue) {
        final float scale = Utils.context().getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * sp 转 px
     * @param spValue
     * @return
     */
    public static int sp2px(float spValue) {
        final float fontScale = Utils.context().getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    /**
     * px 转 sp
     * @param pxValue
     * @return
     */
    public static int px2sp(float pxValue) {
        final float fontScale = Utils.context().getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

}
