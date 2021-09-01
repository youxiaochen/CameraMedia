package you.chen.media.core.scan2;

import com.google.zxing.Result;

import java.util.concurrent.atomic.AtomicBoolean;

import you.chen.media.core.Transform;
import you.chen.media.core.scan.FormatDecoder;
import you.chen.media.utils.LogUtils;

/**
 * Created by Max on 2021/9/1.
 */
public class DecodeThread extends Thread {

    public static final int HANDLE_SUCCESS = 1;

    private final int w, h;
    //扫描解析器
    private final FormatDecoder decoder;
    //要解码的转换处理后的数据
    private final byte[] buffer;

    private DecoderHandler2 handler;

    private final Transform transform;

    private boolean isRunning;

    //是否正在解码
    private final AtomicBoolean isCoding = new AtomicBoolean(false);

    private long currentTag;

    /**
     * Camera.setDisplayOrientation(90), 270; 时 w, h顺序调换
     * @param w
     * @param h
     * @param decoder
     * @param handler
     * @param transform
     */
    public DecodeThread(int w, int h, FormatDecoder decoder, DecoderHandler2 handler, Transform transform) {
        this.w = w;
        this.h = h;
        this.decoder = decoder;
        this.buffer = new byte[w * h * 3 / 2];
        this.handler = handler;
        this.transform = transform;
    }

    public synchronized final boolean push(byte[] data, long currentTag) {
        if (isCoding.get() || !isRunning) {
            return false;
        }
        transform.transform(data, buffer, data.length);
        isCoding.set(true);
        this.currentTag = currentTag;
        notify();
        return true;
    }

    private synchronized void decode() {
        while (!isCoding.get() && isRunning) {
            try {
                LogUtils.i(Thread.currentThread().getName()+"  wait");
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public synchronized void start() {
        isRunning = true;
        super.start();
    }

    public synchronized void quit() {
        isRunning = false;
        notify();
    }

    @Override
    public void run() {
        LogUtils.i("ScanDecoder thread start...");
        while (isRunning) {
            decode();
            if (!isRunning) {
                break;
            }
            long c = System.currentTimeMillis();
            Result result = decoder.decode(buffer, w, h);
            long ss = System.currentTimeMillis() - c;
            LogUtils.i(Thread.currentThread().getName() +"  " + ss +"  " + currentTag);
            if (result != null) {
                handler.sendResult(result, currentTag);
            }
            isCoding.set(false);
        }
        LogUtils.i(Thread.currentThread().getName() + "ScanDecoder quit...");
    }


}
