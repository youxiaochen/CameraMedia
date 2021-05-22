package you.chen.media.core;

/**
 * 编码数据的转换器, 图像NV21相关的一些转换或者音频数据的处理
 * Created by you on 2018-03-20.
 */
public interface Transform {

    /**
     * 将原有数据进行转换
     * @param src
     * @param outs
     */
    void transform(byte[] src, byte[] outs, int len);
}
