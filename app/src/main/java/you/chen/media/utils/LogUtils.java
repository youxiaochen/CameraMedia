package you.chen.media.utils;

import android.util.Log;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Locale;

public final class LogUtils {

    private static final String TAG = "youxiaochen";

    private LogUtils() {}

    public static void i(String msg) {
        Log.i(TAG, msg);
    }

    public static void i(String tag, String msg) {
        Log.i(tag, msg);
    }

    public static void i(String format, Object ...args) {
        i(TAG, String.format(format, args));
    }

    public static void i(String tag, String format, Object ...args) {
        i(tag, String.format(format, args));
    }

    public static void e(Throwable throwable) {
        Log.e(TAG, throwableToString(throwable));
    }

    /**
     * 打印异常信息到log中
     * @param throwable
     * @return
     */
    private static String throwableToString(Throwable throwable) {
        if (throwable == null) {
            return "throwable is null";
        }
        Writer info = new StringWriter();
        PrintWriter printWriter = new PrintWriter(info);
        throwable.printStackTrace(printWriter);
        return info.toString();
    }

}
