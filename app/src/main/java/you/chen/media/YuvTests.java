package you.chen.media;

/**
 * Created by you on 2018-07-06.
 * 简单的nv21与i420,NV12,YV12转换
 */
public class YuvTests {

    public static void nv21ToI420(byte[] nv21, byte[] i420, int w, int h) {
        int frameSize = w * h;
        System.arraycopy(nv21, 0, i420, 0, frameSize);

        int start = frameSize + (frameSize >> 2);
        int i, half;
        for (i = 0; i < frameSize >> 1; i += 2) {
            half = i >> 1;
            i420[frameSize + half] = nv21[frameSize + i + 1];
            i420[start + half] = nv21[frameSize + i];
        }
    }

    public static void nv21ToNV12(byte[] nv21, byte[] nv12, int w, int h) {
        int frameSize = w * h;
        System.arraycopy(nv21, 0, nv12, 0, frameSize);

        int i, start;
        for (i = 0; i < frameSize >> 2; i++) {
            start = frameSize + (i << 1);
            nv12[start] = nv21[start + 1];
            nv12[start + 1] = nv21[start];
        }
    }

    public static void nv21ToYV12(byte[] nv21, byte[] yv12, int w, int h) {
        int frameSize = w * h;
        System.arraycopy(nv21, 0, yv12, 0, frameSize);

        int start = frameSize + (frameSize >> 2);
        int i, half;
        for (i = 0; i < frameSize >> 1; i += 2) {
            half = i >> 1;
            yv12[frameSize + half] = nv21[frameSize + i];
            yv12[start + half] = nv21[frameSize + i + 1];
        }
    }

}
