package you.chen.media.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.TextureView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import you.chen.media.R;

/**
 * Created by you on 2018-04-27.
 */
public class ViewTestActivity extends AppCompatActivity {

    private TextureView tv_test;

    public static void lanuch(Context context) {
        context.startActivity(new Intent(context, ViewTestActivity.class));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_viewtest);
        tv_test = findViewById(R.id.tv_test);
    }






}
