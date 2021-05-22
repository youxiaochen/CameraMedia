package you.chen.media;

import android.app.Application;
import android.content.Context;

import you.chen.media.utils.Utils;

/**
 * Created by you on 2018-01-18.
 */
public class App extends Application {

    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        Utils.init(this);
    }

    public static Context getContext() {
        return context;
    }

}
