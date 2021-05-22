package you.chen.media.core;

import android.media.MediaCodec;

import java.nio.ByteBuffer;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by you on 2018-05-10.
 * MediaCodec核心编码器
 */
public final class MediaEncoder implements Runnable {

    private static final String TAG = MediaEncoder.class.getSimpleName();

    public static final int DEF_TIMEOUT_USEC = 10_000;
    //编码器
    protected final MediaCodec mediaCodec;
    //待编码的数据 Camera.byte[], pcm
    private final BlockingQueue<byte[]> bufferQueue = new LinkedBlockingDeque<>();
    //字节池
    private final BytePool bytePool;
    //数据转换
    private final Transform transform;
    //编码的buffer信息
    private MediaCodec.BufferInfo bufferInfo;
    //是否正在录制待编码
    private final AtomicBoolean isCoding = new AtomicBoolean(false);
    //callback
    private final Callback callback;
    //等待取出编码后的数据时长
    private final int timeoutUs;

    public MediaEncoder(MediaCodec mediaCodec, BytePool bytePool, Transform transform, Callback callback) {
        this(mediaCodec, bytePool, transform, callback, DEF_TIMEOUT_USEC);
    }

    public MediaEncoder(MediaCodec mediaCodec, BytePool bytePool, Transform transform, Callback callback, int timeoutUs) {
        this.mediaCodec = mediaCodec;
        this.bytePool = bytePool;
        this.transform = transform;
        this.callback = callback;
        this.timeoutUs = timeoutUs;
    }

    /**
     * start
     * @param executorService
     */
    public void start(ExecutorService executorService) {
        if (mediaCodec != null) {
            mediaCodec.start();
            isCoding.set(true);
        }
        executorService.execute(this);
    }

    //stop
    public void stop() {
        isCoding.set(false);
        //如果队列已经处于阻塞时,用此唤醒, notify?
        bufferQueue.add(new byte[0]);
    }

    //往里添加数据
    public void push(byte[] data, int len) {
        if (isCoding.get()) {
            byte[] buffer = bytePool.get();
            //LogUtils.i(TAG, "buffQueue un codec  %s  -  %d", callback.getClass().getSimpleName(), bufferQueue.size());
            transform.transform(data, buffer, len);
            bufferQueue.add(buffer);
        }
    }

    public void push(byte[] data) {
        push(data, data.length);
    }

    //释放资源
    private void release() {
        bufferQueue.clear();
        bytePool.clear();
        if (mediaCodec != null) {
            mediaCodec.stop();
            mediaCodec.release();
        }
        callback.onRelease();
    }

    @Override
    public void run() {
        bufferInfo = new MediaCodec.BufferInfo();
        callback.onInitStart();
        while (isCoding.get()) {
            try {
                byte[] buffer = bufferQueue.take();
                if (buffer == null || buffer.length == 0) {
                    break;//用空byte[]来终止循环与阻塞
                }
                codecDatas(buffer);
                //缓存
                bytePool.put(buffer);
            } catch (InterruptedException e) {
                if (!isCoding.get()) {
                    break;
                }
            }
        }
        release();
    }

    /**
     * 编码datas数据
     * @param buffer
     */
    private void codecDatas(byte[] buffer) {
        //加入缓冲区, -1如果当前没有可用的缓冲时会进入阻塞状态, 0时会立刻返回
        int index = mediaCodec.dequeueInputBuffer(-1);
        if (index >= 0) {
            //填充数据
            ByteBuffer inputBuffer = mediaCodec.getInputBuffer(index);
            inputBuffer.clear();
            inputBuffer.put(buffer, 0, buffer.length);

            callback.onEncodeInputBuffer(mediaCodec, buffer, index);
        }
        int encodeStatus;
        while (true) {
            //返回的三种状态 INFO_TRY_AGAIN_LATER, INFO_OUTPUT_FORMAT_CHANGED, INFO_OUTPUT_BUFFERS_CHANGED,
            encodeStatus = mediaCodec.dequeueOutputBuffer(bufferInfo, timeoutUs);
            if (encodeStatus == MediaCodec.INFO_TRY_AGAIN_LATER) {
                break;//稍后重试
            } else if (encodeStatus == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED){
                //这里只会回调一次用于初始化
                callback.onFormatChanged(mediaCodec);
            } else if (encodeStatus == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {
                //忽略
            } else {
                //正常编码获得缓冲下标
                ByteBuffer encodeData = mediaCodec.getOutputBuffer(encodeStatus);
                //写入编码后的数据
                callback.onWriteData(bufferInfo, encodeData);
                //释放缓存冲,后续可以存放新的编码后的数据
                mediaCodec.releaseOutputBuffer(encodeStatus, false);
            }
        }
    }

    /**
     * 编码回调类
     */
    public interface Callback {
        /**
         * 初始化开始
         */
        void onInitStart();

        /**
         * 编码编入数据
         * @param mediaCodec
         * @param buffer
         * @param inputBufferIndex
         */
        void onEncodeInputBuffer(MediaCodec mediaCodec, byte[] buffer, int inputBufferIndex);

        /**
         * MediaCodec初始化
         * @param mediaCodec
         */
        void onFormatChanged(MediaCodec mediaCodec);

        /**
         * 写入数据
         * @param bufferInfo
         * @param encodeData
         */
        void onWriteData(MediaCodec.BufferInfo bufferInfo, ByteBuffer encodeData);

        /**
         * release
         */
        void onRelease();
    }

}
