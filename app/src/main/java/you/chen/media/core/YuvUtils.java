package you.chen.media.core;

/**
 * Created by you on 2018-03-12.
 * 图像数据nv21,i420, nv12, yv12的一些转换裁剪旋转的相关操作
 */
public final class YuvUtils {

    private YuvUtils() {}

    static {
        System.loadLibrary("yuv-utils");
    }

    /**
     * 同时将NV21数据转换成I420并旋转, 不剪切
     * @param nv21 camera datas
     * @param i420 out datas
     * @param w
     * @param h
     * @param orientation
     */
    public static native void nv21ToI420Rotate(byte[] nv21,
                                               byte[] i420,
                                               int w, int h,
                                               @Orientation.OrientationMode int orientation);

    /**
     * 同时剪切NV21数据转换成I420并旋转
     * @param nv21
     * @param i420
     * @param w 相机原尺寸
     * @param h
     * @param cw 需要裁剪后的尺寸,必须都为偶数且 <= w
     * @param ch 同上  <= h
     * @param left
     * @param top
     * @param orientation
     */
    public static native void clipNv21ToI420Rotate(byte[] nv21,
                                                   byte[] i420,
                                                   int w, int h,
                                                   int cw, int ch,
                                                   int left, int top,
                                                   @Orientation.OrientationMode int orientation);

    /**
     * 同时将NV21数据转换成NV12并旋转, 不剪切
     * @param nv21 camera datas
     * @param nv12 out datas
     * @param w
     * @param h
     * @param orientation
     */
    public static native void nv21ToNV12Rotate(byte[] nv21,
                                               byte[] nv12,
                                               int w, int h,
                                               @Orientation.OrientationMode int orientation);

    /**
     * 同时剪切NV21数据转换成NV12并旋转
     * @param nv21
     * @param nv12
     * @param w 相机原尺寸
     * @param h
     * @param cw 需要裁剪后的尺寸,必须都为偶数且 <= w, h
     * @param ch 同上
     * @param left
     * @param top
     * @param orientation
     */
    public static native void clipNv21ToNV12Rotate(byte[] nv21,
                                                   byte[] nv12,
                                                   int w, int h,
                                                   int cw, int ch,
                                                   int left, int top,
                                                   @Orientation.OrientationMode int orientation);

    /**
     * 同时将NV21数据转换成YV12并旋转, 不剪切
     * @param nv21 camera datas
     * @param yv12 out datas
     * @param w
     * @param h
     * @param orientation
     */
    public static native void nv21ToYV12Rotate(byte[] nv21,
                                               byte[] yv12,
                                               int w, int h,
                                               @Orientation.OrientationMode int orientation);

    /**
     * 同时剪切NV21数据转换成YV12并旋转
     * @param nv21
     * @param yv12
     * @param w 相机原尺寸
     * @param h
     * @param cw 需要裁剪后的尺寸,必须都为偶数且 <= w
     * @param ch 同上  <= h
     * @param left
     * @param top
     * @param orientation
     */
    public static native void clipNv21ToYV12Rotate(byte[] nv21,
                                                   byte[] yv12,
                                                   int w, int h,
                                                   int cw, int ch,
                                                   int left, int top,
                                                   @Orientation.OrientationMode int orientation);

    /**
     * 将NV21数据旋转, 不剪切
     * @param nv21 camera datas
     * @param outs out datas
     * @param w
     * @param h
     * @param orientation
     */
    public static native void nv21Rotate(byte[] nv21,
                                         byte[] outs,
                                         int w, int h,
                                         @Orientation.OrientationMode int orientation);

    /**
     * 同时剪切NV21数据并旋转
     * @param nv21
     * @param outs
     * @param w 相机原尺寸
     * @param h
     * @param cw 需要裁剪后的尺寸,必须都为偶数且 <= w
     * @param ch 同上  <= h
     * @param left
     * @param top
     * @param orientation
     */
    public static native void clipNv21Rotate(byte[] nv21,
                                             byte[] outs,
                                             int w, int h,
                                             int cw, int ch,
                                             int left, int top,
                                             @Orientation.OrientationMode int orientation);

}
