
/**
 * Created by you on 2018-09-06. NV21,NV12之间的UV剪切并旋转操作
 * NV21与NV12区别只是UV与VU交差的区别,因此以下方法NV21,NV12可通用
 */
#include "RotateNV_UV.h"

#ifndef CAMERAMEDIACODEC_CONVERTNV_H
#define CAMERAMEDIACODEC_CONVERTNV_H

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
                 libyuv::RotationMode mode);

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
                      const uint8_t* src_uv,
                      int src_stride_uv,
                      uint8_t* dst_y,
                      int dst_stride_y,
                      uint8_t* dst_uv,
                      int dst_stride_uv,
                      int width,
                      int height,
                      libyuv::RotationMode mode);

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
                       libyuv::RotationMode mode);

#endif //CAMERAMEDIACODEC_CONVERTNV_H
