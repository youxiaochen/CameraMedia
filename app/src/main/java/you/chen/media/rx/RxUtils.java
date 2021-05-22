package you.chen.media.rx;

import android.view.View;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import you.chen.media.rx.viewbind.TextViewChangedOnSubscribe;
import you.chen.media.rx.viewbind.ViewClickOnSubscribe;

/**
 * Created by you on 2018/1/11.
 */

public final class RxUtils {

    private RxUtils() {}

    public static void dispose(Disposable...disposables) {
        if (disposables != null && disposables.length > 0) {
            for (Disposable disposable : disposables) {
                if (disposable != null && !disposable.isDisposed()) {
                    disposable.dispose();
                }
            }
        }
    }

    /**
     * 默认防抖一秒
     */
    public static final int DEF_DURATION_CLICK = 1000;

    public static Observable<View> click(View... views) {
        return click(DEF_DURATION_CLICK, views);
    }

    /**
     *
     * @param viewArray 方便BindViews时的参数传入
     * @param views
     * @return
     */
    public static Observable<View> click(View[] viewArray, View... views) {
        return click(DEF_DURATION_CLICK, viewArray, views);
    }

    /**
     * 生成防抖
     * @param clickDuration
     * @param views
     * @return
     */
    public static Observable<View> click(long clickDuration , View... views ) {
        ViewClickOnSubscribe clickOnSubscribe = new ViewClickOnSubscribe();
        clickOnSubscribe.addOnClickListener(views);
        return Observable.create(clickOnSubscribe).throttleFirst(clickDuration , TimeUnit.MILLISECONDS);
    }

    /**
     *
     * @param clickDuration
     * @param viewArray 方便BindViews时的参数传入
     * @param views
     * @return
     */
    public static Observable<View> click(long clickDuration, View[] viewArray, View... views) {
        ViewClickOnSubscribe clickOnSubscribe = new ViewClickOnSubscribe();
        clickOnSubscribe.addOnClickListener(viewArray);
        clickOnSubscribe.addOnClickListener(views);
        return Observable.create(clickOnSubscribe).throttleFirst(clickDuration , TimeUnit.MILLISECONDS);
    }

    public static final int DEF_TIMEOUT = 300;

    public static Observable<String> textChanged(TextView tv) {
        return textChanged(DEF_TIMEOUT, tv);
    }

    public static Observable<String> textChanged(long timeout, TextView tv) {
        TextViewChangedOnSubscribe subscribe = new TextViewChangedOnSubscribe();
        subscribe.addTextViewWatcher(tv);
        return Observable.create(subscribe).debounce(timeout, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread());
    }

    /**
     * 秒倒计时
     * @param second
     * @return
     */
    public static Observable<Long> stimer(long second) {
        return Observable.timer(second, TimeUnit.SECONDS, AndroidSchedulers.mainThread());
    }

    /**
     * 定时发送, 主线程
     * @param second 秒
     * @return
     */
    public static Observable<Long> sinterval(long second) {
        return Observable.interval(second, TimeUnit.SECONDS, AndroidSchedulers.mainThread());
    }

}
