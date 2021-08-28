package you.chen.media.core.mp4;

import android.graphics.Matrix;
import android.media.AudioRecord;
import android.media.MediaCodec;
import android.media.MediaMuxer;
import android.media.MediaRecorder;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.ExecutorService;

import you.chen.media.core.BytePool;
import you.chen.media.core.Constant;
import you.chen.media.core.MediaEncoder;
import you.chen.media.core.MediaUtils;
import you.chen.media.core.Orientation;
import you.chen.media.core.audio.AudioPresentationTime;
import you.chen.media.core.audio.AudioTaker;
import you.chen.media.core.audio.AudioTransform;
import you.chen.media.core.h264.H264Utils;
import you.chen.media.utils.LogUtils;

/**
 * Created by you on 2018-05-16.
 * Muxer混合Mp4
 */
public class Mp4Recorder {

    private final MediaEncoder h264Encoder, audioEncoder;

    private final AudioTaker audioTaker;
    /**
     * 混合器, MediaMuxer在Framework层的实现的代码中都有加锁,有关的方法在线程中可以直接使用
     * Framework中的C++源码方法中都有  Mutex::Autolock autoLock(mMuxerLock);
     */
    private MediaMuxer mediaMuxer;

    private final AudioPresentationTime presentationTime;

    private int h264TrackIndex = -1;

    private int audioTrackIndex = -1;

    private boolean isMuxerStarted = false;

    private boolean h264Released, audioReleased;

    private boolean isRecording = false;

    public Mp4Recorder(String path, int width, int height, Matrix matrix,
                       @Orientation.OrientationMode int orientation) throws IOException {
        this(path, MediaUtils.selectColorFormat(), width, height, matrix, orientation,
                width * height * Constant.VIDEO_BITRATE_COEFFICIENT,
                Constant.FRAME_RATE, Constant.IFRAME_INTERVAL, Constant.SAMPLE_RATE, Constant.CHANNEL_COUNT,
                Constant.AUDIO_RATE, Constant.AAC_PROFILE, Constant.CHANNEL_CONFIG, Constant.AUDIO_FORMAT);
    }

    public Mp4Recorder(String path, int colorFormat,
                       int width, int height, Matrix matrix,
                       @Orientation.OrientationMode int orientation,
                       int h264BitRate, int frameRate, int frameInterval,
                       int sampleRate, int channelCount,
                       int audioBitRate, int aacProfile,
                       int channelConfig, int audioFormat) throws IOException {

        h264Encoder = H264Utils.createH264MediaEncoder(colorFormat, width, height, matrix,
                orientation, h264BitRate, frameRate, frameInterval, new H264Callback());

        int bufferSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat);
        MediaCodec mediaCodec = MediaUtils.createAacMediaCodec(bufferSize, sampleRate, channelCount, audioBitRate, aacProfile);
        audioEncoder = new MediaEncoder(mediaCodec, new BytePool(bufferSize), new AudioTransform(), new AacCallback());
        AudioRecord audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, sampleRate, channelConfig, audioFormat, bufferSize);
        audioTaker = new AudioTaker(audioRecord, bufferSize, audioEncoder);
        presentationTime = new AudioPresentationTime(bufferSize, sampleRate, channelCount, audioFormat);

        mediaMuxer = new MediaMuxer(path, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
    }

    public void start(ExecutorService executorService) {
        isRecording = true;
        h264Encoder.start(executorService);
        audioEncoder.start(executorService);
        audioTaker.start(executorService);
    }

    public synchronized void stop() {
        isRecording = false;
        h264Encoder.stop();
        audioTaker.stop();
        audioEncoder.stop();
        notifyAll();
    }

    //往里添加数据
    public void pushCameraDatas(byte[] data) {
        h264Encoder.push(data);
    }

    /**
     * avc与aac同时都已addTrack时才可开启
     */
    private synchronized void startMuxer() {
        if (!isMuxerStarted && isRecording) {
            if (audioTrackIndex != -1 && h264TrackIndex != -1) {
                mediaMuxer.start();
                isMuxerStarted = true;
                notifyAll();
            } else {
                long c = System.currentTimeMillis();
                do {
                    try {
                        wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } while (isRecording && (audioTrackIndex == -1 || h264TrackIndex == -1));
                long n = System.currentTimeMillis() - c;
                LogUtils.i("wait... %d", n);
            }
        }
    }

    /**
     * aac, h264编码都停止时才可停止
     */
    private synchronized void stopMuxer() {
        if (isMuxerStarted && h264Released && audioReleased) {
            mediaMuxer.stop();
            mediaMuxer.release();
            isMuxerStarted = false;
            mediaMuxer = null;
            LogUtils.i("mp4recorder release...");
        }
    }

    private class H264Callback implements MediaEncoder.Callback {

        @Override
        public void onInitStart() {
            h264TrackIndex = -1;
        }

        @Override
        public void onFormatChanged(MediaCodec mediaCodec) {
            h264TrackIndex = mediaMuxer.addTrack(mediaCodec.getOutputFormat());
            LogUtils.i("Init h264Track... " + h264TrackIndex);
            startMuxer();
        }

        @Override
        public void onEncodeInputBuffer(MediaCodec mediaCodec, byte[] buffer, int inputBufferIndex) {
            mediaCodec.queueInputBuffer(inputBufferIndex, 0, buffer.length,
                    System.nanoTime() / 1000, MediaCodec.BUFFER_FLAG_KEY_FRAME);
        }

        @Override
        public void onWriteData(MediaCodec.BufferInfo bufferInfo, ByteBuffer encodeData) {
            if ((bufferInfo.flags & MediaCodec.BUFFER_FLAG_CODEC_CONFIG) != 0) {
                bufferInfo.size = 0;
            }
            if (bufferInfo.size != 0) {
                encodeData.position(bufferInfo.offset);
                encodeData.limit(bufferInfo.offset + bufferInfo.size);
                mediaMuxer.writeSampleData(h264TrackIndex, encodeData, bufferInfo);
            }
        }

        @Override
        public void onRelease() {
            h264Released = true;
            stopMuxer();
        }
    }

    private class AacCallback implements MediaEncoder.Callback {

        @Override
        public void onInitStart() {
            audioTrackIndex = -1;
            presentationTime.start();
        }

        @Override
        public void onFormatChanged(MediaCodec mediaCodec) {
            audioTrackIndex = mediaMuxer.addTrack(mediaCodec.getOutputFormat());
            LogUtils.i("Init aacTrack... " + audioTrackIndex);
            startMuxer();
        }

        @Override
        public void onEncodeInputBuffer(MediaCodec mediaCodec, byte[] buffer, int inputBufferIndex) {
            mediaCodec.queueInputBuffer(inputBufferIndex, 0, buffer.length, presentationTime.getPresentationTimeUs(), 0);
        }

        @Override
        public void onWriteData(MediaCodec.BufferInfo bufferInfo, ByteBuffer encodeData) {
            if ((bufferInfo.flags & MediaCodec.BUFFER_FLAG_CODEC_CONFIG) != 0) {
                bufferInfo.size = 0;
            }
            if (bufferInfo.size != 0) {
                encodeData.position(bufferInfo.offset);
                encodeData.limit(bufferInfo.offset + bufferInfo.size);
                mediaMuxer.writeSampleData(audioTrackIndex, encodeData, bufferInfo);
            }
        }

        @Override
        public void onRelease() {
            audioReleased = true;
            stopMuxer();
        }
    }

}
