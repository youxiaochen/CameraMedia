package you.chen.media.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import you.chen.media.R;
import you.chen.media.utils.LogUtils;
import you.chen.media.utils.ViewUtils;

/**
 * Created by you on 2018-04-27.
 * 扫码控件
 */
public class ScanFormatView extends View {

    private static final int DEF_SIZE = ViewUtils.dp2px(100);
    //刷新界面的时间
    private static final long ANIMATION_DELAY = 40L;
    //动画移动间距
    private static final int ANIMATION_SIZE = ViewUtils.dp2px(5);

    private int lineHeight = ViewUtils.dp2px(2);

    private Drawable lineDrawable;

    private Rect rect;

    public ScanFormatView(@NonNull Context context) {
        super(context);
        init(context);
    }

    public ScanFormatView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ScanFormatView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        lineDrawable = context.getResources().getDrawable(R.drawable.qrcode_scan_line);
        rect = new Rect();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthModel = MeasureSpec.getMode(widthMeasureSpec);
        int size;//宽高一样
        if (widthModel == MeasureSpec.EXACTLY) {
            size = MeasureSpec.getSize(widthMeasureSpec);
        } else if (MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.EXACTLY) {
            size = MeasureSpec.getSize(heightMeasureSpec);
        } else {
            size = DEF_SIZE;
        }
        int sizeSpec = MeasureSpec.makeMeasureSpec(size, MeasureSpec.EXACTLY);
        super.onMeasure(sizeSpec, sizeSpec);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        rect.set(0, 0, getWidth(), lineHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        lineDrawable.setBounds(rect);
        lineDrawable.draw(canvas);
        rect.offset(0, ANIMATION_SIZE);
        if (rect.bottom  > getHeight()) {
            rect.top = 0;
            rect.bottom = lineHeight;
        }
        postInvalidateDelayed(ANIMATION_DELAY);
    }
}
