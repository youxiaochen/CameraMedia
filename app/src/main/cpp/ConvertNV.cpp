/**
 * Created by you on 2018-09-06. NV21,NV12之间的UV剪切并旋转操作
 * NV21与NV12区别只是UV与VU交差的区别,因此以下方法NV21,NV12可通用
 */

#include "ConvertNV.h"

/**
 * 同时剪切NV21数据并旋转
 * @param sample
 * @param sample_size
 * @param dst_y
 * @param dst_stride_y
 * @param dst_u
 * @param dst_stride_u
 * @param dst_v
 * @param dst_stride_v
 * @param crop_x
 * @param crop_y
 * @param src_width
 * @param src_height
 * @param crop_width
 * @param crop_height
 * @param mode
 */
void ConvertNV21(const uint8_t* sample,
                 uint8_t* dst_y,
                 int dst_stride_y,
                 uint8_t* dst_vu,
                 int dst_stride_vu,
                 int crop_x,
                 int crop_y,
                 int src_width,
                 int src_height,
                 int crop_width,
                 int crop_height,
                 libyuv::RotationMode mode) {
    const uint8_t *src = sample + (src_width * crop_y + crop_x);
    const uint8_t* src_vu = sample + (src_width * src_height) +
                            ((crop_y / 2) * src_width) + ((crop_x / 2) * 2);
    NV21Rotate(src, src_width, src_vu, src_width, dst_y, dst_stride_y,
                     dst_vu, dst_stride_vu, crop_width, crop_height, mode);
}

/**
 * 同时NV21转NV12并旋转
 * @param src_y
 * @param src_stride_y
 * @param src_uv
 * @param src_stride_uv
 * @param dst_y
 * @param dst_stride_y
 * @param dst_uv
 * @param dst_stride_uv
 * @param width
 * @param height
 * @param mode
 */
void NV21ToNV12Rotate(const uint8_t* src_y,
                      int src_stride_y,
                      const uint8_t* src_vu,
                      int src_stride_vu,
                      uint8_t* dst_y,
                      int dst_stride_y,
                      uint8_t* dst_uv,
                      int dst_stride_uv,
                      int width,
                      int height,
                      libyuv::RotationMode mode) {
    if (mode == libyuv::kRotate0) {
        libyuv::NV21ToNV12(src_y, src_stride_y, src_vu, src_stride_vu,
                dst_y, dst_stride_y, dst_uv, dst_stride_uv, width, height);
        return;
    }

    int uv16Width = (width + 1) >> 1;
    int uv16Height = (height + 1) >> 1;

    const uint16_t *src = reinterpret_cast<const uint16_t *>(src_vu);
    int src_stride = (src_stride_vu + 1) >> 1;
    uint16_t *dst = reinterpret_cast<uint16_t *>(dst_uv);
    int dst_stride = (dst_stride_uv + 1) >> 1;

    switch (mode) {
        case libyuv::kRotate90:
            libyuv::RotatePlane90(src_y, src_stride_y, dst_y, dst_stride_y, width, height);
            RotateNV_UV90_X(src, src_stride, dst, dst_stride, uv16Width, uv16Height);//
            break;
        case libyuv::kRotate270:
            libyuv::RotatePlane270(src_y, src_stride_y, dst_y, dst_stride_y, width, height);
            RotateNV_UV270_X(src, src_stride, dst, dst_stride, uv16Width, uv16Height);
            break;
        case libyuv::kRotate180:
            libyuv::RotatePlane180(src_y, src_stride_y, dst_y, dst_stride_y, width, height);
//            RotateNV_UV180_X(src, dst, uv16Width * uv16Height);
            RotateNV_UV180_X(src_vu, src_stride_vu, dst_uv, dst_stride_uv, width, uv16Height);
            break;
    }
}

/**
 * 同时裁剪NV21转NV12并旋转
 * @param sample
 * @param dst_y
 * @param dst_stride_y
 * @param dst_uv
 * @param dst_stride_uv
 * @param crop_x
 * @param crop_y
 * @param src_width
 * @param src_height
 * @param crop_width
 * @param crop_height
 * @param mode
 */
void ConvertNV21ToNV12(const uint8_t* sample,
                       uint8_t* dst_y,
                       int dst_stride_y,
                       uint8_t* dst_uv,
                       int dst_stride_uv,
                       int crop_x,
                       int crop_y,
                       int src_width,
                       int src_height,
                       int crop_width,
                       int crop_height,
                       libyuv::RotationMode mode) {

    const uint8_t *src = sample + (src_width * crop_y + crop_x);
    const uint8_t* src_vu = sample + (src_width * src_height) +
                            ((crop_y / 2) * src_width) + ((crop_x / 2) * 2);
    NV21ToNV12Rotate(src, src_width, src_vu, src_width, dst_y, dst_stride_y,
            dst_uv, dst_stride_uv, crop_width, crop_height, mode);
}
