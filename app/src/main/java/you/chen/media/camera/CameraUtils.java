package you.chen.media.camera;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.PointF;

import you.chen.media.utils.LogUtils;

/**
 *
 * Created by you on 2018/3/24.
 */
final public class CameraUtils {

    private CameraUtils() {}

    /**
     * 计算出控件中坐标点击效果在裁剪旋转90度之前的实际坐标比例,逆旋转90度时,newX = y, newY = w - x
     * @param x
     * @param y
     * @param w
     * @param h
     * @param cameraMatrix
     * @return
     */
    public static PointF reverseRotate(float x, float y, int w, int h, Matrix cameraMatrix) {
        if (cameraMatrix != null) {
            float[] floats = new float[9];//数组0下标为X轴scale, 4为Y轴scale
            cameraMatrix.getValues(floats);
            if (floats[0] == 1.0f && floats[4] > 1.0f) {//Y轴被裁剪
                float preHeight = h * floats[4];//裁剪前的高度
                y = (preHeight - h) / 2 + y;//加上被裁剪的长度
                LogUtils.i("Y轴被裁剪了, x = %f, y = %f", x, y);
                return new PointF(y / preHeight, (w - x) / w);
            } else if (floats[0] > 1.0f && floats[4] == 1.0f) {//X轴被裁剪
                float preWidth = w * floats[0];//裁剪前的宽度
                x = (preWidth - w) / 2 + x;//加上被裁剪的长度
                LogUtils.i("X轴被裁剪了, x = %f, y = %f", x, y);
                return new PointF(y / h, (preWidth - x) / preWidth);
            }
        }
        return new PointF(y / h, (w - x) / w);
    }

    /**
     * 矩阵缩放处理后的大小
     * @param w 原始预览宽
     * @param h 原始预览高
     * @param matrix
     * @return
     */
    public static Point matrixSize(int w, int h, Matrix matrix) {
        if (matrix != null) {
            float[] floats = new float[9];//数组0下标为X轴scale, 4为Y轴scale
            matrix.getValues(floats);

            if (floats[0] == 1.0f && floats[4] > 1.0f) { //Y轴需要裁剪,实则裁剪相机的width
                int clipWidth = (int) (w / floats[4]);
                clipWidth = (clipWidth + 1) & ~1;//裁剪后的宽高都必须为偶数
                if (clipWidth < w) {
                    return new Point(clipWidth, h);
                }
            } else if (floats[0] > 1.0f && floats[4] == 1.0f) {//X轴需要裁剪,实则裁剪相机的height
                int clipHeight = (int) (h / floats[0]);
                clipHeight = (clipHeight + 1) & ~1;//裁剪后的宽高都必须为偶数
                if (clipHeight < h) {
                    return new Point(w, clipHeight);
                }
            }
        }
        return new Point(w, h);
    }

    /**
     * 对原始图片数据裁剪旋转
     * @param datas
     * @param cameraMatrix
     * @param orientation
     * @return
     */
    public static Bitmap bytesToBitmap(byte[] datas, Matrix cameraMatrix, int orientation) {
        Bitmap bitmap = BitmapFactory.decodeByteArray(datas, 0, datas.length);
        Bitmap clipBitmap = null;

        boolean isHorizontal = orientation == 90 || orientation == 270;
        if (cameraMatrix != null) {
            float[] floats = new float[9];//数组0下标为X轴scale, 4为Y轴scale
            cameraMatrix.getValues(floats);
            if (floats[0] == 1.0f && floats[4] > 1.0f) {//Y轴需要裁剪
                //如果为水平旋转的状态时,由于图片经过90或者270度旋转,此时为裁剪X轴
                clipBitmap = clipBitmap(bitmap, floats[4], isHorizontal);
            } else if (floats[0] > 1.0f && floats[4] == 1.0f) {//X轴需要裁剪
                //如果为水平旋转的状态时,由于图片经过90或者270度旋转,此时为裁剪Y轴
                clipBitmap = clipBitmap(bitmap, floats[0], !isHorizontal);
            }
        }

        if (clipBitmap == null) {
            return bitmap;
        }
        bitmap.recycle();
        return clipBitmap;
    }

    /**
     *
     * @param bitmap
     * @param scale 裁剪的比例
     * @param isClipWidth 是否裁剪宽度
     * @return
     */
    private static Bitmap clipBitmap(Bitmap bitmap, float scale, boolean isClipWidth) {
        if (isClipWidth) { //裁剪宽度
            int clipWidth = (int) (bitmap.getWidth() / scale);
            int x = (bitmap.getWidth() - clipWidth) >> 1;
            return Bitmap.createBitmap(bitmap, x, 0, clipWidth, bitmap.getHeight());
        }
        //裁剪高度
        int clipHeight = (int) (bitmap.getHeight() / scale);
        int y = (bitmap.getHeight() - clipHeight) >> 1;
        return Bitmap.createBitmap(bitmap, 0, y, bitmap.getWidth(), clipHeight);
    }

}
