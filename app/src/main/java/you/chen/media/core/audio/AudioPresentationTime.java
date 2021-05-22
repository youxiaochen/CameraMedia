package you.chen.media.core.audio;

import android.media.AudioFormat;

/**
 * Created by you on 2018-05-16.
 * 音频时间采样计算
 * E/MPEG4Writer: timestampUs 6220411 < lastTimestampUs 6220442 for Audio track
 *
 */
public final class AudioPresentationTime {

    private long startTime;

    private final long bufferDurationUs;

    private long currentCount;

    /**
     *
     * @param bufferSize
     * @param sampleRate
     * @param channelCount
     * @param audioFormat
     */
    public AudioPresentationTime(int bufferSize, int sampleRate, int channelCount, int audioFormat) {
        int bitByteSize = audioFormat == AudioFormat.ENCODING_PCM_16BIT ? 2 : 1; //16bit = 2 byte
        bufferDurationUs = 1_000_000L * (bufferSize / (channelCount * bitByteSize)) / sampleRate;
    }

    public void start() {
        startTime = System.nanoTime() / 1000L;
        currentCount = 0;
    }

    public long getPresentationTimeUs() {
        return currentCount++ * bufferDurationUs + startTime;
    }

}
