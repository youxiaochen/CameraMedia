
/**
 * Created by you on 2018-03-06. NV21,NV12之间的UV旋转操作
 * NV21与NV12区别只是UV与VU交差的区别,因此以下方法NV21,NV12可通用
 * 所有_X的交叉即可用作NV12与NV21之间的UV转换
 */

#ifndef CAMERAMEDIACODEC_ROTATENV_UV_H
#define CAMERAMEDIACODEC_ROTATENV_UV_H

#include <libyuv.h>

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
                     int height);

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
                     int height);

/**
 * 镜面翻转uint8_t类型的数据,并交叉变换一个步长的顺序,适合UV与VU之间的镜面转换, 与源码稍作修改
 * @param src
 * @param dst
 * @param width
 */
void MirrorRow_C_X(const uint8_t* src,
                   uint8_t* dst,
                   int width);

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
                int height);

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
                int height);

/**
 * 将NV21或者NV12的UV数据旋转180
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
                    int height);

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
                   int height);

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
                    int height);

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
                      int height);

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
              int height);

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
                enum libyuv::RotationMode mode);

#endif //CAMERAMEDIACODEC_ROTATENV_UV_H