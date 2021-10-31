package you.chen.media.core;

import java.util.LinkedList;

/**
 * 字节池, 在裁剪与编解码时会大量的使用大的byte[],防止内存抖动
 * Created by you on 2018/3/24.
 */
public final class BytePool {

    private static final int DEF_MAX = 10;

    //缓存的字节数组大小, 只缓存一种大小的字节池
    private final int length;
    //最大缓存数量
    private final int maxSize;

    private final LinkedList<byte[]> bytepools = new LinkedList<>();

    public BytePool(int length) {
        this(DEF_MAX, length);
    }
    public BytePool(int maxSize, int length) {
        this.maxSize = maxSize;
        this.length = length;
    }

    public int getLength() {
        return length;
    }

    public synchronized final boolean put(byte[] bytes) {
        if (bytes.length == length && bytepools.size() < maxSize) {
            return bytepools.add(bytes);
        }
        return false;
    }

    public synchronized final byte[] get() {
        if (bytepools.isEmpty()) return new byte[length];
        return bytepools.remove();
    }

    public void clear() {
        bytepools.clear();
    }

}
