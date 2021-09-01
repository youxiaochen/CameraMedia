package you.chen.media.core.scan2;

import android.os.Handler;
import android.os.Message;

import com.google.zxing.Result;

import java.lang.ref.WeakReference;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import androidx.annotation.NonNull;
import you.chen.media.core.Transform;
import you.chen.media.core.scan.FormatDecoder;
import you.chen.media.core.scan.SoundVibratorHelper;
import you.chen.media.utils.LogUtils;
import you.chen.media.utils.Utils;

/**
 * Created by Max on 2021/9/1.
 */
public class DecoderHandler2 extends Handler {

    public static final int HANDLE_SUCCESS = 1;

    private static final int DEF_DECODE_THREAD_COUNT = 3;

    private final DecodeThread[] decodeCore;

    private SoundVibratorHelper soundVibratorHelper;

    private final WeakReference<DecoderCallback> callbackWeakReference;

    private final AtomicLong currentTag  = new AtomicLong(0);

    private final AtomicBoolean isHandleSuccess = new AtomicBoolean(false);

    private int successSleepTime = 2000;

    private boolean successQuit = false;

    public DecoderHandler2(int w, int h, Transform transform, DecoderCallback callback) {
        this(DEF_DECODE_THREAD_COUNT, w, h, transform, new FormatDecoder(), callback);
    }

    public DecoderHandler2(int poolSize, int w, int h, Transform transform, FormatDecoder decoder, DecoderCallback callback) {
        decodeCore = new DecodeThread[poolSize];
        for (int i = 0; i < poolSize; i++) {
            decodeCore[i] = new DecodeThread(w, h, decoder, this, transform);
            decodeCore[i].start();
        }
        callbackWeakReference = new WeakReference<>(callback);
        soundVibratorHelper = new SoundVibratorHelper(Utils.context());
    }

    public void push(byte[] data) {
        if (isHandleSuccess.get()) return;
        for (DecodeThread decodeThread : decodeCore) {
            if (decodeThread.push(data, currentTag.get())) break;;
        }
    }

    public void stop() {
        soundVibratorHelper.stop();
        for (DecodeThread decodeThread : decodeCore) {
            decodeThread.quit();
        }
    }

    final void sendResult(Result result, long tag) {
        boolean isCurrentTag;
        synchronized (this) {
            isCurrentTag = this.currentTag.get() == tag;
            if (isCurrentTag) {
                isHandleSuccess.set(true);
                this.currentTag.incrementAndGet();
            }
        }
        if (isCurrentTag) {
            LogUtils.i(Thread.currentThread().getName() + " success... " + tag);
            sendMessage(obtainMessage(HANDLE_SUCCESS, result));
            if (successQuit) {
                return;
            }
            if (successSleepTime > 0) {
                try {
                    Thread.sleep(successSleepTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            isHandleSuccess.set(false);
        }
    }

    @Override
    public void handleMessage(@NonNull Message msg) {
        if (msg.what == DecodeThread.HANDLE_SUCCESS && msg.obj != null) {
            Result result = (Result) msg.obj;
            DecoderCallback callback = callbackWeakReference.get();
            if (callback != null) {
                soundVibratorHelper.play();
                callback.handleResult(result);
            }
        }
    }


    /**
     * 回调处理
     */
    public interface DecoderCallback {

        void handleResult(Result result);
    }

}
