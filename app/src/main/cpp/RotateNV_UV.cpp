/**
 * Created by you on 2018-03-06. NV21,NV12之间的UV旋转操作
 * NV21与NV12区别只是UV与VU交差的区别,因此以下方法NV21,NV12可通用
 * 所有_X的交叉即可用作NV12与NV21之间的UV转换
 */
#include <libyuv.h>
#include "RotateNV_UV.h"

/**
 * 适合NV21或者NV12的UV数据的操作
 * @param src
 * @param src_stride
 * @param dst
 * @param dst_stride
 * @param width
 * @param height
 */
void TransposeWxH_C2(const uint16_t* src,
                     int src_stride,
                     uint16_t* dst,
                     int dst_stride,
                     int width,
                     int height) {
    int i;
    for (i = 0; i < width; ++i) {
        int j;
        for (j = 0; j < height; ++j) {
            dst[i * dst_stride + j] = src[j * src_stride + i];
        }
    }
}

/**
 * 适合NV21或者NV12的UV数据的操作,并转换UV顺序
 * @param src
 * @param src_stride
 * @param dst
 * @param dst_stride
 * @param width
 * @param height
 */
void TransposeWxH_C2_X(const uint16_t* src,
                       int src_stride,
                       uint16_t* dst,
                       int dst_stride,
                       int width,
                       int height) {
    int i;
    const uint8_t *srcTmp;
    uint8_t *dstTmp;
    for (i = 0; i < width; ++i) {
        int j;
        for (j = 0; j < height; ++j) {
            srcTmp = (const uint8_t *) (src + j * src_stride + i);
            dstTmp = (uint8_t *) (dst + i * dst_stride + j);
            //替换掉uint16_t中的两个uint8_t的顺序, 当然也可以通过位移的方式来替换 (v >> 8) + ((v && 255) << 8)
            dstTmp[0] = srcTmp[1];
            dstTmp[1] = srcTmp[0];
        }
    }
}

/**
 * 镜面翻转uint8_t类型的数据,并交叉变换一个步长的顺序,适合UV与VU之间的镜面转换, 与源码稍作修改
 * @param src
 * @param dst
 * @param width
 */
void MirrorRow_C_X(const uint8_t* src, uint8_t* dst, int width) {
    int x;
    src += width - 1;
    for (x = 0; x < width - 1; x += 2) {
        dst[x] = src[-1];
        dst[x + 1] = src[0];
        src -= 2;
    }
    if (width & 1) {
        dst[width - 1] = src[0];
    }
}

/**
 * 将NV21或者NV12的UV数据旋转90
 * @param src
 * @param src_stride
 * @param dst
 * @param dst_stride
 * @param width UV数据的宽
 * @param height UV数据的高
 */
void RotateNV_UV90(const uint16_t* src,
                   int src_stride,
                   uint16_t* dst,
                   int dst_stride,
                   int width,
                   int height) {
    src += src_stride * (height - 1);
    src_stride = -src_stride;
    TransposeWxH_C2(src, src_stride, dst, dst_stride, width, height);
}

/**
 * 将NV21或者NV12的UV数据旋转270
 * @param src
 * @param src_stride
 * @param dst
 * @param dst_stride
 * @param width UV数据的宽
 * @param height UV数据的高
 */
void RotateNV_UV270(const uint16_t* src,
                    int src_stride,
                    uint16_t* dst,
                    int dst_stride,
                    int width,
                    int height) {
    dst += dst_stride * (width - 1);
    dst_stride = -dst_stride;
    TransposeWxH_C2(src, src_stride, dst, dst_stride, width, height);
}

/**
 * 将NV21或者NV12的UV数据旋转180,修改源码
 * @param src
 * @param src_stride
 * @param dst
 * @param dst_stride
 * @param width
 * @param height
 */
void RotateNV_UV180(const uint8_t* src,
                    int src_stride,
                    uint8_t* dst,
                    int dst_stride,
                    int width,
                    int height) {
    // Swap first and last row and mirror the content. Uses a temporary row.
    align_buffer_64(row, width);
    const uint8_t* src_bot = src + src_stride * (height - 1);
    uint8_t* dst_bot = dst + dst_stride * (height - 1);
    int half_height = (height + 1) >> 1;
    int y;

    // Odd height will harmlessly mirror the middle row twice.
    for (y = 0; y < half_height; ++y) {
        libyuv::CopyRow_C(src, row, width);        // Copy first row into buffer
        //这里用MirrorRow_C_X
        MirrorRow_C_X(src_bot, dst, width);  // Mirror last row into first row
        MirrorRow_C_X(row, dst_bot, width);  // Mirror buffer into last row
        src += src_stride;
        dst += dst_stride;
        src_bot -= src_stride;
        dst_bot -= dst_stride;
    }
    free_aligned_buffer_64(row);
}

/**
 * 同时将NV21或者NV12的UV数据旋转90,并交叉转换UV顺序, 可用作NV12与NV21之间的UV互转
 * @param src
 * @param src_stride
 * @param dst
 * @param dst_stride
 * @param width UV数据的宽
 * @param height UV数据的高
 */
void RotateNV_UV90_X(const uint16_t* src,
                   int src_stride,
                   uint16_t* dst,
                   int dst_stride,
                   int width,
                   int height) {
    src += src_stride * (height - 1);
    src_stride = -src_stride;
    TransposeWxH_C2_X(src, src_stride, dst, dst_stride, width, height);
}

/**
 * 同时将NV21或者NV12的UV数据旋转270, 并交叉转换UV顺序, 可用作NV12与NV21之间的UV互转
 * @param src
 * @param src_stride
 * @param dst
 * @param dst_stride
 * @param width UV数据的宽
 * @param height UV数据的高
 */
void RotateNV_UV270_X(const uint16_t* src,
                    int src_stride,
                    uint16_t* dst,
                    int dst_stride,
                    int width,
                    int height) {
    dst += dst_stride * (width - 1);
    dst_stride = -dst_stride;
    TransposeWxH_C2_X(src, src_stride, dst, dst_stride, width, height);
}

/**
 * 同时将NV21或者NV12的UV数据旋转180, 并交叉转换UV顺序, 可用作NV12与NV21之间的UV互转
 * @param src
 * @param src_stride
 * @param dst
 * @param dst_stride
 * @param width
 * @param height
 */
void RotateNV_UV180_X(const uint8_t* src,
                      int src_stride,
                      uint8_t* dst,
                      int dst_stride,
                      int width,
                      int height) {
    libyuv::RotatePlane180(src, src_stride, dst, dst_stride, width, height);
}

/**
 * NV21的数据拷贝
 * @param src_y
 * @param src_stride_y
 * @param src_vu
 * @param src_stride_vu
 * @param dst_y
 * @param dst_stride_y
 * @param dst_vu
 * @param dst_stride_vu
 * @param width
 * @param height
 */
void NV21Copy(const uint8_t* src_y,
              int src_stride_y,
              const uint8_t* src_vu,
              int src_stride_vu,
              uint8_t* dst_y,
              int dst_stride_y,
              uint8_t* dst_vu,
              int dst_stride_vu,
              int width,
              int height) {
    libyuv::CopyPlane(src_y, src_stride_y, dst_y, dst_stride_y, width, height);
    //整行拷贝,UV高度为Y的一半
    libyuv::CopyPlane(src_vu, src_stride_vu, dst_vu, dst_stride_vu, width, height >> 1);
}

/**
 * NV21旋转
 * @param src_y
 * @param src_stride_y
 * @param src_vu
 * @param src_stride_vu
 * @param dst_y
 * @param dst_stride_y
 * @param dst_vu
 * @param dst_stride_vu
 * @param width
 * @param height
 * @param mode
 */
void NV21Rotate(const uint8_t* src_y,
                int src_stride_y,
                const uint8_t* src_vu,
                int src_stride_vu,
                uint8_t* dst_y,
                int dst_stride_y,
                uint8_t* dst_vu,
                int dst_stride_vu,
                int width,
                int height,
                enum libyuv::RotationMode mode) {
    if (mode == libyuv::kRotate0) { //only copy
        NV21Copy(src_y, src_stride_y, src_vu, src_stride_vu, dst_y, dst_stride_y, dst_vu, dst_stride_vu, width, height);
        return;
    }
    int uv16Width = (width + 1) >> 1;
    int uv16Height = (height + 1) >> 1;

    const uint16_t *src = reinterpret_cast<const uint16_t *>(src_vu);
    int src_stride = (src_stride_vu + 1) >> 1;
    uint16_t *dst = reinterpret_cast<uint16_t *>(dst_vu);
    int dst_stride = (dst_stride_vu + 1) >> 1;

    switch (mode) {
        case libyuv::kRotate90:
            libyuv::RotatePlane90(src_y, src_stride_y, dst_y, dst_stride_y, width, height);
            RotateNV_UV90(src, src_stride, dst, dst_stride, uv16Width, uv16Height);//
            break;
        case libyuv::kRotate270:
            libyuv::RotatePlane270(src_y, src_stride_y, dst_y, dst_stride_y, width, height);
            RotateNV_UV270(src, src_stride, dst, dst_stride, uv16Width, uv16Height);
            break;
        case libyuv::kRotate180: {
            libyuv::RotatePlane180(src_y, src_stride_y, dst_y, dst_stride_y, width, height);
            //参考源码中的参数传入,将width的值增加一倍,即可
            RotateNV_UV180(src_vu, src_stride_vu, dst_vu, dst_stride_vu, width, uv16Height);
            break;
        }
    }
}