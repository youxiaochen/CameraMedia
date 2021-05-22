package you.chen.media.core.audio;

import android.media.AudioRecord;
import android.media.MediaCodec;
import android.media.MediaRecorder;

import java.io.IOException;
import java.util.concurrent.ExecutorService;

import you.chen.media.core.BytePool;
import you.chen.media.core.Constant;
import you.chen.media.core.MediaEncoder;
import you.chen.media.core.MediaUtils;

/**
 * Created by you on 2018-05-13.
 */
public class AudioRecorder {

    public static AudioRecorder createMuxerAudioRecorder(String path) throws IOException {
        return createMuxerAudioRecorder(path, Constant.SAMPLE_RATE, Constant.CHANNEL_COUNT,
                Constant.AUDIO_RATE, Constant.AAC_PROFILE, Constant.CHANNEL_CONFIG, Constant.AUDIO_FORMAT);
    }

    public static AudioRecorder createMuxerAudioRecorder(String path, int sampleRate, int channelCount,
                                                         int audioBitRate, int aacProfile,
                                                         int channelConfig, int audioFormat) throws IOException {
        int bufferSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat);
        MediaCodec mediaCodec = MediaUtils.createAacMediaCodec(bufferSize,
                sampleRate, channelCount, audioBitRate, aacProfile);
        AudioPresentationTime presentationTime = new AudioPresentationTime(bufferSize, sampleRate, channelCount, audioFormat);
        AudioMuxerCallback callback = new AudioMuxerCallback(path, presentationTime);

        MediaEncoder encoder = new MediaEncoder(mediaCodec, new BytePool(bufferSize), new AudioTransform(), callback);
        AudioRecord audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,
                sampleRate, channelConfig, audioFormat, bufferSize);
        AudioTaker audioTaker = new AudioTaker(audioRecord, bufferSize, encoder);
        return new AudioRecorder(encoder, audioTaker);
    }

    @Deprecated
    public static AudioRecorder createAudioRecorder(String path) {
        return createAudioRecorder(path, Constant.SAMPLE_RATE, Constant.CHANNEL_COUNT,
                Constant.AUDIO_RATE, Constant.AAC_PROFILE, Constant.CHANNEL_CONFIG, Constant.AUDIO_FORMAT);
    }

    @Deprecated
    public static AudioRecorder createAudioRecorder(String path, int sampleRate, int channelCount,
                                                    int audioBitRate, int aacProfile,
                                                    int channelConfig, int audioFormat) {
        int bufferSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat);
        MediaCodec mediaCodec = MediaUtils.createAacMediaCodec(bufferSize,
                sampleRate, channelCount, audioBitRate, aacProfile);
        AudioPresentationTime presentationTime = new AudioPresentationTime(bufferSize, sampleRate, channelCount, audioFormat);
        AudioCallback callback = new AudioCallback(path, presentationTime);
        MediaEncoder encoder = new MediaEncoder(mediaCodec, new BytePool(bufferSize), new AudioTransform(), callback);

        AudioRecord audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,
                sampleRate, channelConfig, audioFormat, bufferSize);
        AudioTaker audioTaker = new AudioTaker(audioRecord, bufferSize, encoder);
        return new AudioRecorder(encoder, audioTaker);
    }

    private final MediaEncoder encoder;

    private final AudioTaker audioTaker;

    public AudioRecorder(MediaEncoder encoder, AudioTaker audioTaker) {
        this.encoder = encoder;
        this.audioTaker = audioTaker;
    }

    /**
     * 启动
     * @param service 需要最少2个线程
     */
    public void start(ExecutorService service) {
        encoder.start(service);
        audioTaker.start(service);
    }

    public void stop() {
        audioTaker.stop();
        encoder.stop();
    }

}
