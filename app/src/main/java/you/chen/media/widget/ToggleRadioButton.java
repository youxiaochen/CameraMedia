package you.chen.media.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RadioButton;

/**
 * Created by you on 2018-03-27.
 */
public class ToggleRadioButton extends RadioButton {

    public ToggleRadioButton(Context context) {
        super(context);
    }

    public ToggleRadioButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ToggleRadioButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void toggle() {
        if (!isChecked()) {
            super.toggle();
        } else {
            if (listener != null) {
                listener.onUnToggle(this);
            }
        }
    }

    private OnUnToggleListener listener;

    public void setOnUnToggleListener(OnUnToggleListener listener) {
        this.listener = listener;
    }

    /**
     * 已经选中时的点击响应
     */
    public interface OnUnToggleListener {

        void onUnToggle(ToggleRadioButton button);
    }
}
