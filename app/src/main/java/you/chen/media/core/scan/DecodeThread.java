package you.chen.media.core.scan;

import android.os.Handler;

import com.google.zxing.Result;

import java.util.concurrent.atomic.AtomicBoolean;

import you.chen.media.core.Transform;
import you.chen.media.utils.LogUtils;

/**
 * Created by you on 2018-04-26.
 * 解析线程, 不断的{@link DecodeThread#push(byte[])} 并解析
 */
public class DecodeThread extends Thread {

    public static final int HANDLE_SUCCESS = 1;

    private final int w, h;
    //扫描解析器
    private final FormatDecoder decoder;
    //要解码的转换处理后的数据
    private final byte[] buffer;

    private Handler handler;

    private final Transform transform;

    private boolean isRunning;

    //是否正在解码
    private final AtomicBoolean isCoding = new AtomicBoolean(false);
    //扫描成功后退出
    private final boolean successQuit;

    /**
     * Camera.setDisplayOrientation(90), 270; 时 w, h顺序调换
     * @param w
     * @param h
     * @param decoder
     * @param handler
     * @param transform
     * @param successQuit 需要持续性的扫描时可以设置false
     */
    public DecodeThread(int w, int h, FormatDecoder decoder, Handler handler, Transform transform, boolean successQuit) {
        this.w = w;
        this.h = h;
        this.decoder = decoder;
        this.buffer = new byte[w * h * 3 / 2];
        this.handler = handler;
        this.transform = transform;
        this.successQuit = successQuit;
    }

    public synchronized final void push(byte[] data) {
        if (isCoding.get() || !isRunning) return;
        transform.transform(data, buffer, data.length);
        isCoding.set(true);
        notify();
    }

    private synchronized void decode() {
        while (!isCoding.get() && isRunning) {
            try {
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
            Result result = decoder.decode(buffer, w, h);
            if (result != null) {
                handler.sendMessage(handler.obtainMessage(HANDLE_SUCCESS, result));
                if (successQuit) {
                    isRunning = false;
                    break;
                }
                try {
                    sleep(2000);//需要持续性的扫描功能时,可以在扫描成功时短暂睡眠
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            isCoding.set(false);
        }
        LogUtils.i("ScanDecoder quit...");
    }

}
