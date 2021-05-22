package you.chen.media.core;

import android.media.AudioFormat;
import android.media.MediaCodecInfo;

/**
 * Created by you on 2018-05-19.
 * 多媒体编码的常用参数
 */
public interface Constant {

    // ----------------- H264 -----------------
    int FRAME_RATE = 20; //帧率单位K, Camera中一般支持7~30
    int IFRAME_INTERVAL = 10; //关键帧间隔

    //相机的默认最小与最大帧率参数, 包含 FRAME_RATE 范围
    int DEF_MIN_FPS = 15000;
    int DEF_MAX_FPS = 25000;

    /**
     * 码率系数, w * h * 3, 此参数越大拍出的视频质量越大,最好不超过FRAME_RATE
     */
    int VIDEO_BITRATE_COEFFICIENT = 3;


    //----------------- Audio -----------------

    //双声道
    int CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_STEREO;
    int AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;

    int SAMPLE_RATE = 44100;
    //两声道对应的channelConfig为AudioFormat.CHANNEL_OUT_STEREO
    int CHANNEL_COUNT = 2;
    int AUDIO_RATE = 1000 << 6;
    int AAC_PROFILE = MediaCodecInfo.CodecProfileLevel.AACObjectLC;

}
