package you.chen.media.rx;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;

/**
 * Created by you on 2017/7/5.
 * 请求重试类
 */

public class RequestRetry implements Function<Observable<? extends Throwable>, ObservableSource<?>> {
    /**
     * 默认重试次数
     */
    private static final int DEF_MAXRETRIES = 1;
    /**
     * 默认重试时间间隔
     */
    private static final int DEF_DELAYMILLIS = 2000;
    /**
     * 最大重试次数
     */
    private final int maxRetries;
    /**
     * 重试间隔
     */
    private final int retryDelayMillis;

    private int retryCount;

    public static RequestRetry def() {
        return new RequestRetry(DEF_MAXRETRIES, DEF_DELAYMILLIS);
    }

    public RequestRetry(int maxRetries, int retryDelayMillis) {
        this.maxRetries = maxRetries;
        this.retryDelayMillis = retryDelayMillis;
    }

    @Override
    public ObservableSource<?> apply(Observable<? extends Throwable> observable) throws Exception {
        return observable.flatMap(new Function<Throwable, ObservableSource<?>>() {
            @Override
            public ObservableSource<?> apply(Throwable throwable) throws Exception {
                if (++retryCount <= maxRetries) {
                    return Observable.timer(retryDelayMillis, TimeUnit.MILLISECONDS);
                }
                return Observable.error(throwable);
            }
        });
    }

}
