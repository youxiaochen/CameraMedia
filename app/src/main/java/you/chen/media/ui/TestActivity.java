package you.chen.media.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import you.chen.media.R;
import you.chen.media.camera.OrientationHelper;
import you.chen.media.core.YuvUtils;
import you.chen.media.utils.LogUtils;
import you.chen.media.utils.Utils;

/**
 * Created by you on 2018-01-08.
 */
public class TestActivity extends AppCompatActivity implements View.OnClickListener {

    int width = 12;
    int height = 10;
    int ySize = width * height;
    int size = ySize * 3 / 2;
    byte[] src = new byte[size];

    int clipWidth = 8;
    int clipHeight = 4;

    int top = 2;
    int left = 2;

    //方向传感
    OrientationHelper orientationHelper;

    public static void lanuch(Context context) {
        context.startActivity(new Intent(context, TestActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_test);

        findViewById(R.id.bt1).setOnClickListener(this);
        findViewById(R.id.bt2).setOnClickListener(this);
        findViewById(R.id.bt3).setOnClickListener(this);
        findViewById(R.id.bt4).setOnClickListener(this);
        findViewById(R.id.bt5).setOnClickListener(this);
        findViewById(R.id.bt6).setOnClickListener(this);
        findViewById(R.id.bt7).setOnClickListener(this);
        findViewById(R.id.bt8).setOnClickListener(this);

        orientationHelper = new OrientationHelper(Utils.context());

        for (byte i = 0; i < ySize; i++) {//正数代表Y数据
            src[i] = i;
        }
        for (int i = ySize; i < size; i++) {//负数代表UV数据
            src[i] = (byte) -(i - ySize);
        }

        printfBuf(src);
    }

    @Override
    protected void onResume() {
        super.onResume();
        orientationHelper.enable();
    }

    @Override
    protected void onPause() {
        super.onPause();
        orientationHelper.disable();
    }

    /**
     * 测试时只需要旋转手机即可传入不同的方向参数
     * @param view
     */
    @Override
    public void onClick(View view) {
        byte[] buff = null;
        switch (view.getId()) {
            case R.id.bt1:
                buff = new byte[width * height * 3 / 2];
                YuvUtils.nv21ToI420Rotate(src, buff, width, height, orientationHelper.getOrientation());
                break;
            case R.id.bt2:
                buff = new byte[clipWidth * clipHeight * 3 / 2];
                YuvUtils.clipNv21ToI420Rotate(src, buff, width, height, clipWidth, clipHeight, left, top, orientationHelper.getOrientation());
                break;
            case R.id.bt3:
                buff = new byte[width * height * 3 / 2];
                YuvUtils.nv21ToNV12Rotate(src, buff, width, height, orientationHelper.getOrientation());
                break;
            case R.id.bt4:
                buff = new byte[clipWidth * clipHeight * 3 / 2];
                YuvUtils.clipNv21ToNV12Rotate(src, buff, width, height, clipWidth, clipHeight, left, top, orientationHelper.getOrientation());
                break;
            case R.id.bt5:
                buff = new byte[width * height * 3 / 2];
                YuvUtils.nv21ToYV12Rotate(src, buff, width, height, orientationHelper.getOrientation());
                break;
            case R.id.bt6:
                buff = new byte[clipWidth * clipHeight * 3 / 2];
                YuvUtils.clipNv21ToYV12Rotate(src, buff, width, height, clipWidth, clipHeight, left, top, orientationHelper.getOrientation());
                break;
            case R.id.bt7:
                buff = new byte[width * height * 3 / 2];
                YuvUtils.nv21Rotate(src, buff, width, height, orientationHelper.getOrientation());
                break;
            case R.id.bt8:
                buff = new byte[clipWidth * clipHeight * 3 / 2];
                YuvUtils.clipNv21Rotate(src, buff, width, height, clipWidth, clipHeight, left, top, orientationHelper.getOrientation());
                break;
        }
        printfBuf(buff);
    }

    private void printfBuf(byte[] buff) {
        int ySize = buff.length * 2 / 3;
        StringBuilder sb = new StringBuilder("YData:");
        for (int i = 0; i < ySize; i++) {
            sb.append(buff[i]).append(" ");
        }
        sb.append('\n').append("UVdata:");
        for (int i = ySize; i < buff.length; i++) {
            sb.append(buff[i]).append(" ");
        }
        LogUtils.i(sb.toString());
    }

}
