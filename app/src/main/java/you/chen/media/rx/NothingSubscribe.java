package you.chen.media.rx;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * Created by you on 2018/7/30.
 */

public abstract class NothingSubscribe<T> implements Observer<T> {

    private Disposable disposable;

    @Override
    public void onSubscribe(Disposable disposable) {
        this.disposable = disposable;
    }

    @Override
    public void onError(Throwable e) {
        RxUtils.dispose(disposable);
    }

    @Override
    public void onComplete() {
        RxUtils.dispose(disposable);
    }

}
