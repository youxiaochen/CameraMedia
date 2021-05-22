package you.chen.media.core.audio;

import android.media.MediaCodec;
import android.media.MediaMuxer;

import java.io.IOException;
import java.nio.ByteBuffer;

import you.chen.media.core.MediaEncoder;

/**
 * Created by you on 2018-05-19.
 *
 */
public class AudioMuxerCallback implements MediaEncoder.Callback {

    //混合器
    private final MediaMuxer mediaMuxer;

    private final AudioPresentationTime presentationTime;

    private int audioTrack;

    private boolean isMuxerStarted;

    public AudioMuxerCallback(String path, AudioPresentationTime presentationTime) throws IOException {
        mediaMuxer = new MediaMuxer(path, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
        this.presentationTime = presentationTime;
    }

    @Override
    public void onInitStart() {
        audioTrack = -1;
        isMuxerStarted = false;

        presentationTime.start();
    }

    @Override
    public void onFormatChanged(MediaCodec mediaCodec) {
        audioTrack = mediaMuxer.addTrack(mediaCodec.getOutputFormat());
        mediaMuxer.start();
        isMuxerStarted = true;
    }

    @Override
    public void onEncodeInputBuffer(MediaCodec mediaCodec, byte[] buffer, int inputBufferIndex) {
        mediaCodec.queueInputBuffer(inputBufferIndex, 0, buffer.length, presentationTime.getPresentationTimeUs(), 0);
    }

    @Override
    public void onWriteData(MediaCodec.BufferInfo bufferInfo, ByteBuffer encodeData) {
        if ((bufferInfo.flags & MediaCodec.BUFFER_FLAG_CODEC_CONFIG) != 0) {
            bufferInfo.size = 0;
        }
        if (bufferInfo.size != 0) {
            encodeData.position(bufferInfo.offset);
            encodeData.limit(bufferInfo.offset + bufferInfo.size);
            mediaMuxer.writeSampleData(audioTrack, encodeData, bufferInfo);
        }
    }

    @Override
    public void onRelease() {
        if (isMuxerStarted) {
            mediaMuxer.stop();
            mediaMuxer.release();
        }
    }

}
