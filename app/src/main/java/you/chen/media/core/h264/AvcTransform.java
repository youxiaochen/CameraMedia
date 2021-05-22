package you.chen.media.core.h264;

import android.media.MediaCodecInfo;

import you.chen.media.core.Orientation;
import you.chen.media.core.Transform;
import you.chen.media.core.YuvUtils;

/**
 * 相机NV21数据转换操作
 */
public class AvcTransform implements Transform {

    /**
     * 转换的宽与高
     */
    private final int w, h;
    //MediaCodec colorFormat
    private final int colorFormat;

    @Orientation.OrientationMode
    private final int orientation;

    public AvcTransform(int w, int h, int colorFormat, @Orientation.OrientationMode int orientation) {
        this.w = w;
        this.h = h;
        this.colorFormat = colorFormat;
        this.orientation = orientation;
    }

    /**
     *
     * @param nv21 相机出来的数据
     * @param outs 转变后的数据储存数组
     */
    @Override
    public void transform(byte[] nv21, byte[] outs, int len) {
        switch (colorFormat) {
            case MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Planar:
                YuvUtils.nv21ToI420Rotate(nv21, outs, w, h, orientation);
                break;
            case MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420SemiPlanar:
                YuvUtils.nv21ToNV12Rotate(nv21, outs, w, h, orientation);
                break;
            case MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420PackedPlanar:
                YuvUtils.nv21ToYV12Rotate(nv21, outs, w, h, orientation);
                break;
            default:
                YuvUtils.nv21Rotate(nv21, outs, w, h, orientation);
                break;
        }
    }

}
