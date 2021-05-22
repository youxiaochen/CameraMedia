package you.chen.media.ui;

import android.Manifest;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import you.chen.media.R;
import you.chen.media.rx.perm.PermissionMustCallback;
import you.chen.media.rx.perm.RxPermissions;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    RxPermissions permissions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_main);

        permissions = new RxPermissions(this);
        findViewById(R.id.bt1).setOnClickListener(this);
        findViewById(R.id.bt2).setOnClickListener(this);
        findViewById(R.id.bt3).setOnClickListener(this);
        findViewById(R.id.bt4).setOnClickListener(this);
        findViewById(R.id.bt5).setOnClickListener(this);
        findViewById(R.id.bt6).setOnClickListener(this);
        findViewById(R.id.bt7).setOnClickListener(this);
        findViewById(R.id.bt8).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt1:
                permissions.requestEach(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA)
                        .subscribe(new PermissionMustCallback() {
                            @Override
                            protected void permissionCallback(boolean isGranted) {
                                CameraActivity.lanuch(MainActivity.this);
                            }
                        });
                break;
            case R.id.bt2:
                permissions.requestEach(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA)
                        .subscribe(new PermissionMustCallback() {
                            @Override
                            protected void permissionCallback(boolean isGranted) {
                                H264Activity.lanuch(MainActivity.this);
                            }
                        });
                break;
            case R.id.bt3:
                TestActivity.lanuch(this);
                break;
            case R.id.bt4:
                permissions.requestEach(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.RECORD_AUDIO)
                        .subscribe(new PermissionMustCallback() {
                            @Override
                            protected void permissionCallback(boolean isGranted) {
                                AacActivity.lanuch(MainActivity.this);
                            }
                        });
                break;
            case R.id.bt5:
                permissions.requestEach(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.CAMERA)
                        .subscribe(new PermissionMustCallback() {
                            @Override
                            protected void permissionCallback(boolean isGranted) {
                                Mp4Activity.lanuch(MainActivity.this);
                            }
                        });
                break;
            case R.id.bt6:
                permissions.requestEach(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA)
                        .subscribe(new PermissionMustCallback() {
                            @Override
                            protected void permissionCallback(boolean isGranted) {
                                ScanActivity.lanuch(MainActivity.this);
                            }
                        });
                break;
            case R.id.bt7:
                ViewTestActivity.lanuch(this);
                break;
            case R.id.bt8:
                permissions.requestEach(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.RECORD_AUDIO)
                        .subscribe(new PermissionMustCallback() {
                            @Override
                            protected void permissionCallback(boolean isGranted) {

                            }
                        });
                break;
        }
    }

}
