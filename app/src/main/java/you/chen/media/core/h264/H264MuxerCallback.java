package you.chen.media.core.h264;

import android.media.MediaCodec;
import android.media.MediaMuxer;

import java.io.IOException;
import java.nio.ByteBuffer;

import you.chen.media.core.MediaEncoder;

/**
 * Created by you on 2018-05-10.
 * Muxer时间对齐的H264
 */
public class H264MuxerCallback  implements MediaEncoder.Callback {

    //混合器
    private final MediaMuxer mediaMuxer;

    private int h264TrackIndex = -1;

    private boolean isMuxerStarted = false;

    public H264MuxerCallback(String path) throws IOException {
        mediaMuxer = new MediaMuxer(path, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
    }

    @Override
    public void onInitStart() {
        h264TrackIndex = -1;
        isMuxerStarted = false;
    }

    @Override
    public void onEncodeInputBuffer(MediaCodec mediaCodec, byte[] buffer, int inputBufferIndex) {
        mediaCodec.queueInputBuffer(inputBufferIndex, 0, buffer.length,
                System.nanoTime() / 1000, MediaCodec.BUFFER_FLAG_KEY_FRAME);
    }

    @Override
    public void onFormatChanged(MediaCodec mediaCodec) {
        h264TrackIndex = mediaMuxer.addTrack(mediaCodec.getOutputFormat());
        mediaMuxer.start();
        isMuxerStarted = true;
    }

    @Override
    public void onWriteData(MediaCodec.BufferInfo bufferInfo, ByteBuffer encodeData) {
        if ((bufferInfo.flags & MediaCodec.BUFFER_FLAG_CODEC_CONFIG) != 0) {
            bufferInfo.size = 0;
        }
        if (bufferInfo.size != 0) {
            encodeData.position(bufferInfo.offset);
            encodeData.limit(bufferInfo.offset + bufferInfo.size);
            mediaMuxer.writeSampleData(h264TrackIndex, encodeData, bufferInfo);
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
