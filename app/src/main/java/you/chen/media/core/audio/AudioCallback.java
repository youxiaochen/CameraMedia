package you.chen.media.core.audio;

import android.media.MediaCodec;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import you.chen.media.core.MediaEncoder;
import you.chen.media.utils.FileUtils;
import you.chen.media.utils.LogUtils;

/**
 * Created by you on 2018-05-19.
 * @deprecated use {@link AudioMuxerCallback}
 */
@Deprecated
public class AudioCallback implements MediaEncoder.Callback {

    //BufferInfo中的大小不固定,可以用大小固定的缓冲数组写出
    public static final int WRITE_BUFFER_SIZE = 1024;

    private final String path;
    //音频时间计算器
    private final AudioPresentationTime presentationTime;

    //储存数据
    private FileOutputStream fos;
    private BufferedOutputStream bos;
    //写入数据缓冲
    private byte[] writeBuffer;
    //aac header
    private byte[] adtsHeader = new byte[7];

    //aac header
    private static final int profile = 2;
    private static final int freqIdx = 4;//对应的44100 H
    private static final int chanCfg = 2;

    public AudioCallback(String path, AudioPresentationTime presentationTime) {
        this.path = path;
        this.presentationTime = presentationTime;
    }

    @Override
    public void onInitStart() {
        writeBuffer = new byte[WRITE_BUFFER_SIZE];
        //header前三位是固定
        adtsHeader[0] = (byte) 0xFF;
        adtsHeader[1] = (byte) 0xF9;
        adtsHeader[2] = (byte) (((profile - 1) << 6) + (freqIdx << 2) + (chanCfg >> 2));

        presentationTime.start();
    }

    @Override
    public void onFormatChanged(MediaCodec mediaCodec) {
        try {
            fos = new FileOutputStream(path);
            bos = new BufferedOutputStream(fos);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onEncodeInputBuffer(MediaCodec mediaCodec, byte[] buffer, int inputBufferIndex) {
        mediaCodec.queueInputBuffer(inputBufferIndex, 0, buffer.length, presentationTime.getPresentationTimeUs(), 0);
    }

    @Override
    public void onWriteData(MediaCodec.BufferInfo bufferInfo, ByteBuffer encodeData) {
        if (bufferInfo.size != 0) {
            encodeData.position(bufferInfo.offset);
            encodeData.limit(bufferInfo.offset + bufferInfo.size);

            addADTStoPacket(bufferInfo.size + 7);
            try {
                bos.write(adtsHeader, 0, 7);
            } catch (IOException e) {
                e.printStackTrace();
            }

            //将ByteBuffer中的数据写到文件中
            LogUtils.i("write buffinfosize  %d", bufferInfo.size);
            int offset = bufferInfo.offset;
            int bufferSize = bufferInfo.size;
            while (bufferSize > writeBuffer.length) {
                writeByteBuffer(encodeData, offset, writeBuffer.length);
                bufferSize -= writeBuffer.length;
                offset += writeBuffer.length;
            }
            if (bufferSize > 0) {
                writeByteBuffer(encodeData, offset, bufferSize);
            }
            //byte[] buf = new byte[bufferInfo.size];
            //encodeData.get(buf); 不能用此种方式写入,内存抖动极大
        }
    }

    @Override
    public void onRelease() {
        FileUtils.closeCloseable(bos, fos);
    }

    private void addADTStoPacket(int packetLen) {
        adtsHeader[3] = (byte) (((chanCfg & 3) << 6) + (packetLen >> 11));
        adtsHeader[4] = (byte) ((packetLen & 0x7FF) >> 3);
        adtsHeader[5] = (byte) (((packetLen & 7) << 5) + 0x1F);
        adtsHeader[6] = (byte) 0xFC;
    }

    /**
     * 将ByteBuffer通过byte[]写入到文件
     * @param encodeData
     * @param offset
     * @param length
     */
    private void writeByteBuffer(ByteBuffer encodeData, int offset, int length) {
        encodeData.position(offset);
        encodeData.limit(offset + length);

        encodeData.get(writeBuffer, 0, length);
        try {
            bos.write(writeBuffer, 0, length);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
