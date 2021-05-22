package you.chen.media.core.audio;

import you.chen.media.core.Transform;

/**
 * Created by you on 2018-03-20.
 * 暂不做处理
 */
public class AudioTransform implements Transform {

    @Override
    public void transform(byte[] src, byte[] outs, int len) {
        System.arraycopy(src, 0, outs, 0, len);
    }
}
