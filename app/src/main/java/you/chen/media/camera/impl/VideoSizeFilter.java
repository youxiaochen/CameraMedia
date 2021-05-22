package you.chen.media.camera.impl;

import android.hardware.Camera;

import java.util.ArrayList;
import java.util.List;

import you.chen.media.camera.SizeFilter;


/**
 * 常用的尺寸筛选, 优先采用尺寸限制(适用于尺寸比较固定的需求)
 * 优先筛选尺寸最接近, 再筛选宽高比例最接近的
 * Created by you on 2018/3/24.
 */
public class VideoSizeFilter implements SizeFilter {

    //默认固定最大尺寸,此参数已属于高清,若节省流量适配早期低配手机可以设置 540 * 960
    public static final int DEF_MAX_WIDTH = 720;
    public static final int DEF_MAX_HEIGHT = 1280;

    private final int maxWidth, maxHeight;

    public VideoSizeFilter() {
        this(DEF_MAX_WIDTH, DEF_MAX_HEIGHT);
    }

    public VideoSizeFilter(int maxWidth, int maxHeight) {
        this.maxWidth = maxWidth;
        this.maxHeight = maxHeight;
    }

    /**
     * 拍摄视频可忽略Picture尺寸
     * @param outs
     * @param w
     * @param h
     * @return
     */
    @Override
    public Camera.Size findOptimalPicSize(List<Camera.Size> outs, int w, int h) {
        return null;
    }

    /**
     * 查找出最佳预览尺寸,优先找出尺寸小于并最接近最大限制的尺寸
     * @param outs
     * @param picSize
     * @param w
     * @param h
     * @return
     */
    @Override
    public Camera.Size findOptimalPreSize(List<Camera.Size> outs, Camera.Size picSize, int w, int h) {
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
     * 优先找出尺寸小于最大限制的尺寸
     * @param outs
     * @return
     */
    private List<Camera.Size> filterSize(List<Camera.Size> outs) {
        if (outs == null || outs.isEmpty()) return null;
        List<Camera.Size> filterSizes = new ArrayList<>();//最佳的筛选
        List<Camera.Size> secondarySizes = new ArrayList<>();//次要的筛选
        for (Camera.Size size : outs) {
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
