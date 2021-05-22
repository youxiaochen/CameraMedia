package you.chen.media.widget;

import android.content.Context;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;

import java.util.List;

import androidx.annotation.Nullable;
import you.chen.media.R;
import you.chen.media.utils.ViewUtils;

/**
 * Created by you on 2018-03-26.
 */
public class FlashView extends LinearLayout implements
        RadioGroup.OnCheckedChangeListener, ToggleRadioButton.OnUnToggleListener {


    private ImageView iv_flash;

    private RadioGroup rg_flash;

    private ToggleRadioButton rb_close, rb_open, rb_auto, rb_light;

    public FlashView(Context context) {
        super(context);
        init(context);
    }

    public FlashView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public FlashView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        this.setOrientation(HORIZONTAL);
        this.setGravity(Gravity.CENTER_VERTICAL);

        iv_flash = new ImageView(context);
        int padding = ViewUtils.dp2px(13);
        iv_flash.setPadding(padding, 0, padding, 0);
        iv_flash.setImageResource(R.drawable.flash_camera);
        iv_flash.setOnClickListener(v -> rg_flash.setVisibility(View.VISIBLE));
        this.addView(iv_flash, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

        LayoutInflater.from(context).inflate(R.layout.include_flash, this);
        rg_flash = findViewById(R.id.rg_flash);
        rb_close = rg_flash.findViewById(R.id.rb_close);
        rb_open = rg_flash.findViewById(R.id.rb_open);
        rb_auto = rg_flash.findViewById(R.id.rb_auto);
        rb_light = rg_flash.findViewById(R.id.rb_light);

        rg_flash.setOnCheckedChangeListener(this);
        rb_close.setOnUnToggleListener(this);
        rb_open.setOnUnToggleListener(this);
        rb_auto.setOnUnToggleListener(this);
        rb_light.setOnUnToggleListener(this);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        rg_flash.setVisibility(View.INVISIBLE);
        switch (checkedId) {
            case R.id.rb_close:
                iv_flash.setSelected(false);
                if (listener != null) {
                    listener.onFlashChanged(Camera.Parameters.FLASH_MODE_OFF);
                }
                break;
            case R.id.rb_open:
                iv_flash.setSelected(true);
                if (listener != null) {
                    listener.onFlashChanged(Camera.Parameters.FLASH_MODE_ON);
                }
                break;
            case R.id.rb_auto:
                iv_flash.setSelected(false);
                if (listener != null) {
                    listener.onFlashChanged(Camera.Parameters.FLASH_MODE_AUTO);
                }
                break;
            case R.id.rb_light:
                iv_flash.setSelected(true);
                if (listener != null) {
                    listener.onFlashChanged(Camera.Parameters.FLASH_MODE_TORCH);
                }
                break;
        }

    }

    @Override
    public void onUnToggle(ToggleRadioButton button) {
        rg_flash.setVisibility(View.INVISIBLE);
    }

    public void setFlashModes(List<String> flashModes, boolean isFront) {
        if (isFront || flashModes == null || flashModes.size() < 2) {
            this.setVisibility(View.GONE);
            return;
        }
        //一定会有关闭功能
        this.setVisibility(View.VISIBLE);
        iv_flash.setSelected(false);
        rb_close.setChecked(true);
        rb_open.setVisibility(flashModes.contains(Camera.Parameters.FLASH_MODE_ON) ? View.VISIBLE : View.GONE);
        rb_auto.setVisibility(flashModes.contains(Camera.Parameters.FLASH_MODE_AUTO) ? View.VISIBLE : View.GONE);
        rb_light.setVisibility(flashModes.contains(Camera.Parameters.FLASH_MODE_TORCH) ? View.VISIBLE : View.GONE);
    }

    private OnFlashChangedListener listener;

    public void setOnFlashChangedListener(OnFlashChangedListener listener) {
        this.listener = listener;
    }

    public interface OnFlashChangedListener {
        void onFlashChanged(String model);
    }

}
