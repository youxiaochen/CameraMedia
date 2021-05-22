package you.chen.media.camera.impl;

import android.hardware.Camera;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import you.chen.media.camera.SizeFilter;

/**
 * 高清拍照, 只需要找出最大的尺寸即可,不推荐使用
 * Created by you on 2018/3/24.
 */
public class HightSizeFilter implements SizeFilter {

    private CameraCompare compare;

    public HightSizeFilter() {
        compare = new CameraCompare();
    }

    @Override
    public Camera.Size findOptimalPicSize(List<Camera.Size> outs, int w, int h) {
        if (outs != null && !outs.isEmpty()) {
            Collections.sort(outs, compare);
            return outs.get(0);
        }
        return null;
    }

    /**
     * 优先找出宽高比例最按近pic的, 再找出尺寸最接近surface的
     * @param outs
     * @param picSize
     * @param w
     * @param h
     * @return
     */
    @Override
    public Camera.Size findOptimalPreSize(List<Camera.Size> outs, Camera.Size picSize, int w, int h) {
        if (outs == null || outs.isEmpty()) return null;

        float targetScale = picSize.width / (float) picSize.height;
        long targetSize = w * (long) h;

        Camera.Size optimalSize = null;
        float optimalDiff = Float.MAX_VALUE; //宽高比例与pic的差
        long optimalMaxDif = Long.MAX_VALUE; //最优的最大值差距

        for (Camera.Size size : outs) {
            float newDiff = Math.abs(size.width / (float) size.height - targetScale);
            if (newDiff < optimalDiff) {//更好的比例
                optimalDiff = newDiff;
                optimalSize = size;
                optimalMaxDif = Math.abs(targetSize - size.width * (long) size.height);
            } else if (newDiff == optimalDiff) {
                long newOptimalMaxDif = Math.abs(targetSize - size.width * (long) size.height);
                if (newOptimalMaxDif < optimalMaxDif) {
                    optimalSize = size;
                    optimalMaxDif = newOptimalMaxDif;
                }
            }
        }
        return optimalSize;
    }

    private static class CameraCompare implements Comparator<Camera.Size> {
        @Override
        public int compare(Camera.Size o1, Camera.Size o2) {
            return Long.signum((long) o2.width * o2.height - (long) o1.width * o1.height);
        }
    }

}
