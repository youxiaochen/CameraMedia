package you.chen.media.utils;

import android.os.Environment;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;


/**
 * Created by you on 2016/12/2.
 */

public final class FileUtils {

    private FileUtils() {
    }

    /**
     * 缓存文件根目录名
     */
    private static final String FILE_DIR = "youxiaochen";

    /**
     * SD卡是否存在
     */
    public static boolean isSDCardExist() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    /**
     * 获取缓存目录路径
     *
     * @return
     */
    public static String getCacheDirPath() {
        if (isSDCardExist()) {
            String path = Environment.getExternalStorageDirectory() + File.separator + FILE_DIR + File.separator;
            File directory = new File(path);
            if (!directory.exists()) directory.mkdirs();
            return path;
        } else {
            File directory = new File(Utils.context().getCacheDir(), FileUtils.FILE_DIR);
            if (!directory.exists()) directory.mkdirs();
            return directory.getAbsolutePath();
        }
    }

    /**
     * 获取缓存目录
     *
     * @return
     */
    public static File getCacheDir() {
        if (isSDCardExist()) {
            String path = Environment.getExternalStorageDirectory() + File.separator + FILE_DIR + File.separator;
            File directory = new File(path);
            if (!directory.exists()) directory.mkdirs();
            return directory;
        } else {
            File directory = new File(Utils.context().getCacheDir(), FileUtils.FILE_DIR);
            if (!directory.exists()) directory.mkdirs();
            return directory;
        }
    }

    /**
     * 关闭资源
     *
     * @param closees
     */
    public static void closeCloseable(Closeable...closees) {
        for (Closeable close : closees) {
            if (close != null) {
                try {
                    close.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
