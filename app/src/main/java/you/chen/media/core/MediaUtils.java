package you.chen.media.core;

import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaCodecList;
import android.media.MediaFormat;

import java.io.IOException;

import you.chen.media.utils.LogUtils;

/**
 * Created by you on 2018-05-10.
 * MediaCodec Utils
 */
final public class MediaUtils {

    private MediaUtils() {}

    //创建AVC MediaCodec
    public static MediaCodec createAvcMediaCodec(int w, int h, int colorFormat,
                                                 @Orientation.OrientationMode int orientation,
                                                 int bitRate, int frameRate, int frameInterval) {
        MediaFormat format;
        if (orientation == Orientation.ROTATE90 || orientation == Orientation.ROTATE270) {
            format = MediaFormat.createVideoFormat(MediaFormat.MIMETYPE_VIDEO_AVC, h, w);
        } else {
            format = MediaFormat.createVideoFormat(MediaFormat.MIMETYPE_VIDEO_AVC, w, h);
        }
        //色彩空间
        format.setInteger(MediaFormat.KEY_COLOR_FORMAT, colorFormat);
        format.setInteger(MediaFormat.KEY_BIT_RATE, bitRate);
        format.setInteger(MediaFormat.KEY_FRAME_RATE, frameRate);
        format.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, frameInterval);

        try {
            MediaCodec mediaCodec = MediaCodec.createEncoderByType(MediaFormat.MIMETYPE_VIDEO_AVC);
            mediaCodec.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
            return mediaCodec;
        } catch (IOException e) {
            LogUtils.e(e);
        }
        return null;
    }

    //创建AAC MediaCodec
    public static MediaCodec createAacMediaCodec(int bufferSize, int sampleRate,
                                                 int channelCount, int bitRate, int aacProfile) {
        MediaFormat format = MediaFormat.createAudioFormat(MediaFormat.MIMETYPE_AUDIO_AAC, sampleRate, channelCount);
        format.setInteger(MediaFormat.KEY_MAX_INPUT_SIZE, bufferSize);
        format.setInteger(MediaFormat.KEY_BIT_RATE, bitRate);
        format.setInteger(MediaFormat.KEY_AAC_PROFILE, aacProfile);
        try {
            MediaCodec mediaCodec = MediaCodec.createEncoderByType(MediaFormat.MIMETYPE_AUDIO_AAC);
            mediaCodec.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
            return mediaCodec;
        } catch (IOException e) {
            LogUtils.e(e);
        }
        return null;
    }

    public static MediaCodecInfo selectCodecInfo(String mime) {
        int numCodecs = MediaCodecList.getCodecCount();
        for (int i = 0; i < numCodecs; i++) {
            MediaCodecInfo codecInfo = MediaCodecList.getCodecInfoAt(i);
            if (!codecInfo.isEncoder()) {
                continue;
            }
            String[] types = codecInfo.getSupportedTypes();
            for (int j = 0; j < types.length; j++) {
                if (types[j].equalsIgnoreCase(mime)) {
                    return codecInfo;
                }
            }
        }
        return null;
    }

    //查询支持的输入格式
    public static int selectColorFormat() {
        MediaCodecInfo codecInfo = selectCodecInfo(MediaFormat.MIMETYPE_VIDEO_AVC);
        if (codecInfo == null) {
            return -1;
        }
        MediaCodecInfo.CodecCapabilities capabilities = codecInfo.getCapabilitiesForType(MediaFormat.MIMETYPE_VIDEO_AVC);
        int[] colorFormats = capabilities.colorFormats;
        for (int i = 0; i < colorFormats.length; i++) {
            if (colorFormats[i] == MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Planar
                    || colorFormats[i] == MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420SemiPlanar
                    || colorFormats[i] == MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420PackedSemiPlanar
                    || colorFormats[i] == MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420PackedPlanar) {
                return colorFormats[i];
            }
        }
        return -1;
    }

}
