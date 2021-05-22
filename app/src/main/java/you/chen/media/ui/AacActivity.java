package you.chen.media.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import androidx.appcompat.app.AppCompatActivity;
import you.chen.media.R;
import you.chen.media.core.audio.AudioRecorder;
import you.chen.media.utils.FileUtils;

/**
 * Created by you on 2018-03-10.
 */
public class AacActivity extends AppCompatActivity implements View.OnClickListener {

    TextView bt;

    CheckBox cb_muxer;

    AudioRecorder recorder;

    //线程池执行
    ExecutorService service;

    public static void lanuch(Context context) {
        context.startActivity(new Intent(context, AacActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_aac);
        service = Executors.newFixedThreadPool(2);

        initView();
    }

    private void initView() {
        cb_muxer = findViewById(R.id.cb_muxer);
        bt = findViewById(R.id.bt);
        bt.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt:
                if (!bt.isSelected()) {//start
                    startRecording();
                } else {
                    stopRecording();
                }
                break;
        }
    }

    private void startRecording() {
        String saveName = cb_muxer.isChecked() ? "audioMuxer.aac" : "audioTest.aac";
        String path = FileUtils.getCacheDirPath()  + saveName;
        try {
            recorder = cb_muxer.isChecked() ? AudioRecorder.createMuxerAudioRecorder(path) : AudioRecorder.createAudioRecorder(path);
            recorder.start(service);
        } catch (IOException e) {
            e.printStackTrace();
        }


        bt.setSelected(true);
        bt.setText("end");
    }

    private void stopRecording() {
        if (recorder != null) {
            recorder.stop();
            recorder = null;
        }
        bt.setSelected(false);
        bt.setText("start");
    }
}
