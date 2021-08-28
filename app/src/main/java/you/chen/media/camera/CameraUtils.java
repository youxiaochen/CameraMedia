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
     * 旋转Bitmap
     * @param bitmap
     * @param degree
     * @return
     */
    public static Bitmap rotateBitmap(Bitmap bitmap, int degree) {
        if (degree == 0) return bitmap;
        Matrix matrix = new Matrix();
        matrix.setRotate(degree);
        Bitmap newBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        bitmap.recycle();
        return newBitmap;
    }

    /**
     * 剪切并旋转角度
     * @param bitmap
     * @param x
     * @param y
     * @param degree
     * @return
     */
    public static Bitmap rotateClipBitmap(Bitmap bitmap, int x, int y, int width, int height, int degree) {
        Matrix matrix = null;
        if (degree > 0) {
            matrix = new Matrix();
            matrix.setRotate(degree);
        }
        Bitmap clipBitmap = Bitmap.createBitmap(bitmap, x, y, width, height, matrix, true);
        bitmap.recycle();
        return clipBitmap;
    }

    /**
     * 考虑到在Camera中直接设置parameters.setRotation(getCameraRotation(orientation));
     * 对部分机型无效,因此统一采用预览拍照后再作旋转
     * 对原始图片数据裁剪旋转
     * @param datas
     * @param cameraMatrix
     * @param orientation
     * @return
     */
    public static Bitmap bytesToBitmap(byte[] datas, Matrix cameraMatrix, int orientation) {
        Bitmap bitmap = BitmapFactory.decodeByteArray(datas, 0, datas.length);
        orientation += 90;//需要在原有之上增加旋转的角度
        if (orientation >= 360) orientation = 0;
        if (cameraMatrix != null) {
            float[] floats = new float[9];//数组0下标为X轴scale, 4为Y轴scale
            cameraMatrix.getValues(floats);
            if (floats[0] == 1.0f && floats[4] > 1.0f) {//Y轴需要裁剪,实际为width需要裁剪
                int clipWidth = (int) (bitmap.getWidth() / floats[4]);
                int x = (bitmap.getWidth() - clipWidth) >> 1;
                return rotateClipBitmap(bitmap, x, 0, clipWidth, bitmap.getHeight(), orientation);
            } else if (floats[0] > 1.0f && floats[4] == 1.0f) {//X轴需要裁剪,实际为height需要裁剪
                int clipHeight = (int) (bitmap.getHeight() / floats[0]);
                int y = (bitmap.getHeight() - clipHeight) >> 1;
                return rotateClipBitmap(bitmap, 0, y, bitmap.getWidth(), clipHeight, orientation);
            }
        }
        //不需要裁剪
        return rotateBitmap(bitmap, orientation);
    }
}
