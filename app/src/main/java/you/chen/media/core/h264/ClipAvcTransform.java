package you.chen.media.core.h264;

import android.media.MediaCodecInfo;

import you.chen.media.core.Orientation;
import you.chen.media.core.Transform;
import you.chen.media.core.YuvUtils;
import you.chen.media.utils.LogUtils;

/**
 * 相机NV21数据剪切转换操作
 */
public class ClipAvcTransform implements Transform {
    /**
     * 转换后的宽与高
     */
    private final int w, h;
    //MediaCodec colorFormat
    private final int colorFormat;

    @Orientation.OrientationMode
    private final int orientation;

    /**
     * 原始预览的图片宽与高
     */
    private final int width, height;
    /**
     * 裁剪的点坐标
     */
    private final int left, top;

    public ClipAvcTransform(int w, int h, int width, int height, int colorFormat,
                            @Orientation.OrientationMode int orientation) {
        this.w = w;
        this.h = h;
        this.colorFormat = colorFormat;
        this.orientation = orientation;
        this.width = width;
        this.height = height;

        this.left = w < width ? (((width - w) / 2 + 1) & ~1) : 0;//偏移也必须为偶数
        this.top = h < height ? (((height - h) / 2 + 1) & ~1) : 0;
        LogUtils.i("%d - %d, %d - %d, %d - %d, %d", width, height, w, h, left, top, colorFormat);
    }

    @Override
    public void transform(byte[] nv21, byte[] outs, int len) {
        switch (colorFormat) {
            case MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Planar:
                YuvUtils.clipNv21ToI420Rotate(nv21, outs, width, height, w, h, left, top, orientation);
                break;
            case MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420SemiPlanar:
                YuvUtils.clipNv21ToNV12Rotate(nv21, outs, width, height, w, h, left, top, orientation);
                break;
            case MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420PackedPlanar:
                YuvUtils.clipNv21ToYV12Rotate(nv21, outs, width, height, w, h, left, top, orientation);
                break;
            default:
                YuvUtils.clipNv21Rotate(nv21, outs, width, height, w, h, left, top, orientation);
                break;
        }
    }

}
