package you.chen.media.rx.perm;


import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import you.chen.media.rx.RxUtils;

/**
 * Created by you on 2018/4/19.
 * 必要的权限请求,所请求的权限必须获取
 */

public abstract class PermissionMustCallback implements Observer<Permission> {

    private boolean isGranted = true;

    private Disposable permissionDisposable;

    @Override
    public void onSubscribe(Disposable disposable) {
        permissionDisposable = disposable;
    }

    @Override
    public void onComplete() {
        permissionCallback(isGranted);
        RxUtils.dispose(permissionDisposable);
    }

    @Override
    public void onError(Throwable e) {
        RxUtils.dispose(permissionDisposable);
    }

    @Override
    public void onNext(Permission permission) {
        isGranted &= permission.granted;
    }

    protected abstract void permissionCallback(boolean isGranted);

}
