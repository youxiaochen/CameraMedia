package you.chen.media.rx.viewbind;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.TextView;

import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.functions.Cancellable;

/**
 * Created by you on 2017/4/10.
 */

public class TextViewChangedOnSubscribe implements ObservableOnSubscribe<String> {

    private TextView mTextView;

    public void addTextViewWatcher(TextView mTextView) {
        this.mTextView = mTextView;
    }

    @Override
    public void subscribe(final ObservableEmitter<String> emitter) throws Exception {
        final TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!emitter.isDisposed()) {
                    emitter.onNext(s.toString().trim());
                }
            }
        };
        mTextView.addTextChangedListener(watcher);
        emitter.setCancellable(new Cancellable() {
            @Override
            public void cancel() throws Exception {
                mTextView.removeTextChangedListener(watcher);
            }
        });
    }

}
