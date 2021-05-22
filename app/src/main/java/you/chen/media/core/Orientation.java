package you.chen.media.core;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import androidx.annotation.IntDef;

/**
 * Created by you on 2018-03-10.
 * 图像数据旋转角度
 */
public interface Orientation {

    @IntDef({ROTATE0, ROTATE90, ROTATE180, ROTATE270})
    @Retention(RetentionPolicy.SOURCE)
    @interface OrientationMode {}

    /**
     * 此四种旋转的值与JNI中的RotationMode值对应
     */
    int ROTATE0 = 0;//不旋转
    int ROTATE90 = 90;
    int ROTATE180 = 180;
    int ROTATE270 = 270;
}
