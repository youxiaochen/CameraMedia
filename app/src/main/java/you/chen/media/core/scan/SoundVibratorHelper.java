package you.chen.media.core.scan;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Vibrator;

import you.chen.media.R;

/**
 * Created by you on 2018-04-26.
 * 扫描成功时的声音震动操作
 */
public final class SoundVibratorHelper {
    /**
     * 振动时间
     */
    private static final long DEF_VIBRATE_DURATION = 200L;

    private SoundPool soundPool;

    private int soundId;
    /**
     * 振动
     */
    private Vibrator vibrator;

    public SoundVibratorHelper(Context context) {
        context = context.getApplicationContext();
        vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);

        SoundPool.Builder builder = new SoundPool.Builder();
        builder.setMaxStreams(2);

        AudioAttributes.Builder attrBuilder = new AudioAttributes.Builder();
        attrBuilder.setLegacyStreamType(AudioManager.STREAM_NOTIFICATION);
        builder.setAudioAttributes(attrBuilder.build());
        soundPool = builder.build();

        soundId = soundPool.load(context, R.raw.beep, 1);
    }

    public void play() {
        vibrator.cancel();
        vibrator.vibrate(DEF_VIBRATE_DURATION);
        soundPool.play(soundId, 1, 1, 0, 0, 1);
    }

    public void stop() {
        soundPool.release();
        vibrator.cancel();
    }

}
