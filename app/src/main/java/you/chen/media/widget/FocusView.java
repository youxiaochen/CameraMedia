package you.chen.media.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import you.chen.media.R;

/**
 * Created by you on 2018-03-26.
 */
public class FocusView extends FrameLayout {

    private static final int DEF_SIZE = 200;

    private ImageView iv_focus;

    private Animation animation;

    public FocusView(@NonNull Context context) {
        super(context);
        init(context);
    }

    public FocusView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public FocusView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        iv_focus = new ImageView(context);
        iv_focus.setScaleType(ImageView.ScaleType.FIT_XY);
        iv_focus.setImageResource(R.drawable.focus_camera);
        iv_focus.setVisibility(View.INVISIBLE);
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        addView(iv_focus, params);
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
    protected void onDetachedFromWindow() {
        iv_focus.clearAnimation();
        super.onDetachedFromWindow();
    }

    /**
     * 设置当前聚焦中心坐标点
     * @param x
     * @param y
     */
    public final void setCenter(float x, float y) {
        View parent = (View) getParent();
        int left = parent.getPaddingLeft();
        int right = parent.getWidth() - parent.getPaddingRight() - getWidth();
        int top = parent.getPaddingTop();
        int bottom = parent.getHeight() - parent.getPaddingBottom() - getHeight();

        x = x - getWidth() / 2.0f + parent.getPaddingLeft();
        y = y - getHeight() / 2.0f + parent.getPaddingTop();
        //不能超过边缘
        x = clamp(x, left, right);
        y = clamp(y, top, bottom);
        setX(x);
        setY(y);

        iv_focus.clearAnimation();
        if (animation == null) {
            animation = initAnim();
        }
        iv_focus.startAnimation(animation);
    }

    /**
     * x值不能超出min~max范围
     */
    private float clamp(float x, int min, int max) {
        if (x > max) return max;
        if (x < min) return min;
        return x;
    }

    private Animation initAnim() {
        AnimationSet animation = new AnimationSet(false);

        ScaleAnimation scaleAnim = new ScaleAnimation(1.f, 0.5f, 1.f, 0.5f,
                iv_focus.getWidth() / 2f, iv_focus.getHeight() / 2f);
        scaleAnim.setDuration(300);
        scaleAnim.setFillAfter(true);
        AlphaAnimation alphaAnim = new AlphaAnimation(1.f, 0.75f);
        alphaAnim.setDuration(700);

        animation.addAnimation(scaleAnim);
        animation.addAnimation(alphaAnim);

        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                iv_focus.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                iv_focus.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        return animation;
    }

}
