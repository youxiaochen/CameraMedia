package you.chen.media.core.scan;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.PlanarYUVLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.ResultPointCallback;
import com.google.zxing.common.HybridBinarizer;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Set;

/**
 * Created by you on 2018-04-26.
 * 扫描解析器
 */
public class FormatDecoder {

    private MultiFormatReader formatReader;

    /**
     *
     * //根据实际需求添加BarcodeFormat, 此构造只支持二维码与各种条形码
     * BarcodeFormat.DATA_MATRIX, BarcodeFormat.AZTEC, BarcodeFormat.PDF_417
     *
     */
    public FormatDecoder() {
        this(EnumSet.of(BarcodeFormat.QR_CODE,
                BarcodeFormat.UPC_A,
                BarcodeFormat.UPC_E,
                BarcodeFormat.EAN_13,
                BarcodeFormat.EAN_8,
                BarcodeFormat.RSS_14,
                BarcodeFormat.RSS_EXPANDED,
                BarcodeFormat.CODE_39,
                BarcodeFormat.CODE_93,
                BarcodeFormat.CODE_128,
                BarcodeFormat.ITF,
                BarcodeFormat.CODABAR), null, null);
    }

    public FormatDecoder(Set<BarcodeFormat> formatSet, String characterSet, ResultPointCallback callback) {
        formatReader = new MultiFormatReader();
        EnumMap<DecodeHintType, Object> hints = new EnumMap<>(DecodeHintType.class);
        hints.put(DecodeHintType.POSSIBLE_FORMATS, formatSet);
        if (characterSet != null) {
            hints.put(DecodeHintType.CHARACTER_SET, characterSet);
        }
        if (callback != null) {
            hints.put(DecodeHintType.NEED_RESULT_POINT_CALLBACK, callback);
        }
        formatReader.setHints(hints);
    }

    /**
     * 解码camera datas
     * @param datas
     * @param w
     * @param h
     * @return
     */
    public Result decode(byte[] datas, int w, int h) {
        PlanarYUVLuminanceSource source = new PlanarYUVLuminanceSource(datas, w, h, 0, 0, w, h, false);
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
        try {
            return formatReader.decodeWithState(bitmap);
        } catch (Exception e) {
//            LogUtils.e(e);
        } finally {
            formatReader.reset();
        }
        return null;
    }

}
