package you.chen.media.utils;

import android.content.Context;

import androidx.annotation.NonNull;

/**
 * Created by you on 2017-02-20.
 * for context
 */
public final class Utils {

    private static Context context;

    public static void init(@NonNull Context context) {
        if (Utils.context != null) return;
        Utils.context = context.getApplicationContext();
    }

    public static Context context() {
        if (context == null) {
            throw new NullPointerException("MediaUtils context must be init");
        }
        return context;
    }

}
