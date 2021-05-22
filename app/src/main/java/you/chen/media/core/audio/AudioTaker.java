package you.chen.media.core.audio;

import android.media.AudioRecord;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

import you.chen.media.core.MediaEncoder;
import you.chen.media.utils.LogUtils;

/**
 * Created by you on 2018-05-10.
 * AudioRecord 取PCM数据
 */
public class AudioTaker implements Runnable {

    //语音录制
    private final AudioRecord audioRecord;
    //编码器
    private final MediaEncoder encoder;
    //缓冲数组
    private final byte[] buffer;
    //是否正在录制
    private final AtomicBoolean isRecording = new AtomicBoolean(false);

    public AudioTaker(AudioRecord audioRecord, int bufferSize, MediaEncoder encoder) {
        this.audioRecord = audioRecord;
        this.buffer = new byte[bufferSize];
        this.encoder = encoder;
    }

    /**
     * 在线程执行runnable前调用start
     */
    public final void start(ExecutorService service) {
        audioRecord.startRecording();
        isRecording.set(true);
        service.execute(this);
    }

    public final void stop() {
        isRecording.set(false);
    }

    @Override
    public void run() {
        while (isRecording.get()) {
            int len = audioRecord.read(buffer, 0, buffer.length);
            if (len > 0) {
                encoder.push(buffer, len);
            }
        }
        release();
    }

    /**
     * 释放资源
     */
    private void release() {
        LogUtils.i("audio release...");
        if (audioRecord != null) {
            audioRecord.stop();
            audioRecord.release();
        }
    }

}
