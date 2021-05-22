package you.chen.media.core.h264;

import android.media.MediaCodec;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import you.chen.media.core.MediaEncoder;
import you.chen.media.utils.FileUtils;

/**
 * Created by you on 2018-05-10.
 * h264是没有时间戳概念的，就是一堆流文件,需要播放速度统一可以使用MediaMuxer混合器进行时间戳对齐
 * @deprecated To use {@link H264MuxerCallback}
 */
@Deprecated
public final class H264Callback implements MediaEncoder.Callback {

    //BufferInfo中的大小不固定,可以用大小固定的缓冲数组写出
    public static final int WRITE_BUFFER_SIZE = 1024 << 4;
    //储存数据
    private final String path;
    private FileOutputStream fos;
    private BufferedOutputStream bos;
    //写入数据缓冲
    private byte[] writeBuffer;

    public H264Callback(String path) {
        this.path = path;
    }

    @Override
    public void onInitStart() {
        writeBuffer = new byte[WRITE_BUFFER_SIZE];
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
        mediaCodec.queueInputBuffer(inputBufferIndex, 0, buffer.length,
                System.nanoTime() / 1000, MediaCodec.BUFFER_FLAG_KEY_FRAME);
    }

    @Override
    public void onWriteData(MediaCodec.BufferInfo bufferInfo, ByteBuffer encodeData) {
        if (bufferInfo.size != 0) {
            //将ByteBuffer中的数据写到文件中
//            LogUtils.i("write buffinfosize  %d", bufferInfo.size);
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
