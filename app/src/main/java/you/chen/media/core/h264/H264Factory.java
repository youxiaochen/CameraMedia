package you.chen.media.core.h264;

import android.graphics.Matrix;
import android.graphics.Point;
import android.media.MediaCodec;

import you.chen.media.camera.CameraUtils;
import you.chen.media.core.BytePool;
import you.chen.media.core.Constant;
import you.chen.media.core.MediaEncoder;
import you.chen.media.core.MediaUtils;
import you.chen.media.core.Orientation;
import you.chen.media.core.Transform;

/**
 * Created by you on 2018-05-19.
 */
public final class H264Factory {

    private H264Factory() {}

    public static MediaEncoder createH264MediaEncoder(int width, int height, Matrix matrix,
                                                      @Orientation.OrientationMode int orientation,
                                                      MediaEncoder.Callback callback) {
        int h264BitRate = width * height * Constant.VIDEO_BITRATE_COEFFICIENT;
        return createH264MediaEncoder(MediaUtils.selectColorFormat(), width, height, matrix,
                orientation, h264BitRate, Constant.FRAME_RATE, Constant.IFRAME_INTERVAL, callback);
    }

    public static MediaEncoder createH264MediaEncoder(int colorFormat, int width, int height, Matrix matrix,
                                                      @Orientation.OrientationMode int orientation,
                                                      int h264BitRate, int frameRate, int frameInterval,
                                                      MediaEncoder.Callback callback) {
        Point matrixSize = CameraUtils.matrixSize(width, height, matrix);
        BytePool bytePool = new BytePool(matrixSize.x * matrixSize.y * 3 / 2);
        MediaCodec h264Codec = MediaUtils.createAvcMediaCodec(matrixSize.x, matrixSize.y,
                colorFormat, orientation, h264BitRate, frameRate, frameInterval);
        Transform transform = matrixSize.equals(width, height) ?
                new AvcTransform(width, height, colorFormat, orientation)
                : new ClipAvcTransform(matrixSize.x, matrixSize.y, width, height, colorFormat, orientation);
        return new MediaEncoder(h264Codec, bytePool, transform, callback);
    }

}
