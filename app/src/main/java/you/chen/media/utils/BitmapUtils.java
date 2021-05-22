package you.chen.media.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public final class BitmapUtils {

    private BitmapUtils() {}

    public static boolean saveBitmap(Bitmap bitmap, File file) {
        return saveBitmap(bitmap, file, false);
    }

    public static boolean saveBitmap(Bitmap bmp, File file, boolean recycle) {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            return true;
        }catch (IOException e) {
            LogUtils.e(e);
        } finally {
            if (recycle) {
                bmp.recycle();
            }
            FileUtils.closeCloseable(fos);
        }
        return false;
    }

    //NV21转bitmap
    public static Bitmap nv21ToBitmap(byte[] nv21, int width, int height) {
        Bitmap bitmap = null;
        try {
            YuvImage image = new YuvImage(nv21, ImageFormat.NV21, width, height, null);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            image.compressToJpeg(new Rect(0, 0, width, height), 100, stream);
            bitmap = BitmapFactory.decodeByteArray(stream.toByteArray(), 0, stream.size());
            stream.close();
        } catch (IOException e) {
            LogUtils.e(e);
        }
        return bitmap;
    }

}
