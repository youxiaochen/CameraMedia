package you.chen.media.camera.impl;

import android.hardware.Camera;

import java.util.ArrayList;
import java.util.List;

import you.chen.media.camera.SizeFilter;
import you.chen.media.utils.LogUtils;

/**
 * 常用于拍照时预览与图片尺寸对齐,先选出图片尺寸,再根据图片尺寸筛选预览尺寸
 * 预览时优先筛选尺寸比例最接近的, 再筛选宽高大小最接近的
 * Created by you on 2018/3/24.
 */
public class PictureSizeFilter implements SizeFilter {
    //默认固定最大尺寸
    public static final int DEF_MAX_WIDTH = 1080;
    public static final int DEF_MAX_HEIGHT = 1920;

    private final int maxWidth, maxHeight;

    public PictureSizeFilter() {
        this(DEF_MAX_WIDTH, DEF_MAX_HEIGHT);
    }

    public PictureSizeFilter(int maxWidth, int maxHeight) {
        this.maxWidth = maxWidth;
        this.maxHeight = maxHeight;
    }

    /**
     * 筛选尺寸最接近最大限制的,再找出比例最接近的
     * @param outs
     * @param w
     * @param h
     * @return
     */
    @Override
    public Camera.Size findOptimalPicSize(List<Camera.Size> outs, int w, int h) {
        List<Camera.Size> filterSize = filterSize(outs);
        if (filterSize == null || filterSize.isEmpty()) return null;
        Camera.Size optimalSize = null;

        float targetScale = h / (float) w;
        int minDiffSize = Integer.MAX_VALUE;
        int maxDiffSize = Integer.MAX_VALUE;
        float optimalScale = Float.MAX_VALUE;

        for (Camera.Size size : filterSize) {
            float newOptimalScale = Math.abs(size.width / (float) size.height - targetScale);
            int diffWidth = Math.abs(maxWidth - size.height);
            int diffHeight = Math.abs(maxHeight - size.width);
            //先找出宽高与最大宽高需求的最小与最大差值
            int newMinDiffSize, newMaxDiffSize;
            if (diffWidth > diffHeight) {
                newMinDiffSize = diffHeight;
                newMaxDiffSize = diffWidth;
            } else {
                newMinDiffSize = diffWidth;
                newMaxDiffSize = diffHeight;
            }

            if (newMinDiffSize < minDiffSize) {
                optimalSize = size;
                minDiffSize = newMinDiffSize;
                maxDiffSize = newMaxDiffSize;
                optimalScale = newOptimalScale;
            } else if (newMinDiffSize == minDiffSize) {
                if (newMaxDiffSize < maxDiffSize) {
                    optimalSize = size;
                    maxDiffSize = newMaxDiffSize;
                    optimalScale = newOptimalScale;
                } else if (newMaxDiffSize == maxDiffSize) {
                    if (newOptimalScale < optimalScale) {
                        optimalSize = size;
                        optimalScale = newOptimalScale;
                    }
                }
            }
        }
        return optimalSize;
    }

    /**
     * 优先找出宽高比例最按近pic的, 再找出尺寸最接近picSize的, 最后找接近maxWidth, maxHeight的
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
        long targetSize = picSize.width * (long) picSize.height;
        long targetMaxSize = maxWidth * (long) maxHeight;

        Camera.Size optimalSize = null;
        float optimalDiff = Float.MAX_VALUE; //宽高比例与pic的差
        long optimalSizeDiff = Long.MAX_VALUE; //最优的尺寸差
        long optimalMaxDiff = Long.MAX_VALUE;//最优的与最大值的尺寸差

        for (Camera.Size size : outs) {
//            LogUtils.i("preSize %d - %d", size.width, size.height);
            float newDiff = Math.abs(size.width / (float) size.height - targetScale);
            long newSize = size.width * (long) size.height;
            if (newDiff < optimalDiff) {//更好的比例
                optimalSize = size;
                optimalDiff = newDiff;
                optimalSizeDiff = Math.abs(targetSize - newSize);
                optimalMaxDiff = Math.abs(targetMaxSize - newSize);
            } else if (newDiff == optimalDiff) {//一样的比例再挑出尺寸最接近的
                long newOptimalSizeDiff = Math.abs(targetSize - newSize);
                if (newOptimalSizeDiff < optimalSizeDiff) {
                    optimalSize = size;
                    optimalSizeDiff = newOptimalSizeDiff;
                    optimalMaxDiff = Math.abs(targetMaxSize - newSize);
                } else if (newOptimalSizeDiff == optimalSizeDiff) { //找出最接近maxWidth, maxHeight
                    long newOptimalMaxSizeDiff = Math.abs(targetMaxSize - newSize);
                    if (newOptimalMaxSizeDiff < optimalMaxDiff) {
                        optimalSize = size;
                        optimalMaxDiff = newOptimalMaxSizeDiff;
                    }
                }
            }
        }
        return optimalSize;
    }

    /**
     * 优先找出尺寸小于最大限制的尺寸
     * @param outs
     * @return
     */
    private List<Camera.Size> filterSize(List<Camera.Size> outs) {
        if (outs == null || outs.isEmpty()) return null;
        List<Camera.Size> filterSizes = new ArrayList<>();//最佳的筛选
        List<Camera.Size> secondarySizes = new ArrayList<>();//次要的筛选
        for (Camera.Size size : outs) {
//            LogUtils.i("picSize %d - %d", size.width, size.height);
            if (size.width <= maxHeight && size.height <= maxWidth) {
                filterSizes.add(size);
            } else if (size.width <= maxHeight || size.height <= maxWidth) {
                secondarySizes.add(size);
            }
        }
        if (!filterSizes.isEmpty()) {
            return filterSizes;
        }
        if (!secondarySizes.isEmpty()) {
            return secondarySizes;
        }
        return outs;
    }

}
