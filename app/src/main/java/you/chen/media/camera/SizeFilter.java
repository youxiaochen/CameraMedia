package you.chen.media.camera;

import android.hardware.Camera;

import java.util.List;

/**
 * pic与preview 尺寸筛选器
 * Created by you on 2018/3/24.
 */
public interface SizeFilter {

    /**
     * 查找出最合适的图片尺寸, 视频可以忽略
     * @param outs
     * @param w
     * @param h
     * @return
     */
    Camera.Size findOptimalPicSize(List<Camera.Size> outs, int w, int h);

    /**
     * 查找出最合适的预览尺寸, 一般先确认拍摄Pic的尺寸
     * @param outs
     * @param picSize
     * @param w
     * @param h
     * @return
     */
    Camera.Size findOptimalPreSize(List<Camera.Size> outs, Camera.Size picSize, int w, int h);
}
