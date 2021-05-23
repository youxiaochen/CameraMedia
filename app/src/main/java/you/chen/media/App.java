package you.chen.media;

import android.app.Application;

import you.chen.media.utils.Utils;

/**
 * Created by you on 2018-01-18.
 */
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Utils.init(this);
    }

}
