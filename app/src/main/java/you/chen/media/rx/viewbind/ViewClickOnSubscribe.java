package you.chen.media.rx.viewbind;

import android.view.View;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.functions.Cancellable;

/**
 * Created by you on 2016/10/19.
 */

public class ViewClickOnSubscribe implements ObservableOnSubscribe<View> {

    /**
     * 注册防抖点击的控件
     */
    private List<View> clickViews = new ArrayList<View>();

    /**
     * 添加控件点击事件
     * @param views
     */
    public void addOnClickListener(View... views) {
        if (views == null) return;
        for (View v : views) {
            clickViews.add(v);
        }
    }

    @Override
    public void subscribe(final ObservableEmitter<View> emitter) throws Exception {
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!emitter.isDisposed()) {
                    emitter.onNext(v);
                }
            }
        };
        for (View v : clickViews) {
            v.setOnClickListener(listener);
        }
        emitter.setCancellable(new Cancellable() {
            @Override
            public void cancel() throws Exception {
                Iterator<View> iterator = clickViews.iterator();
                while (iterator.hasNext()) {
                    iterator.next().setOnClickListener(null);
                    iterator.remove();
                }
            }
        });
    }

}
