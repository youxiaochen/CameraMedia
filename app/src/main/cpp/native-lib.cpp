#include <jni.h>
#include <libyuv.h>
#include <assert.h>
#include <memory.h>

#include "ConvertNV.h"
#include "Log.h"

using namespace libyuv;

/**
 * Created by you on 2018-03-06
 */
extern "C" JNIEXPORT void JNICALL
Java_you_chen_media_core_YuvUtils_nv21ToI420Rotate(JNIEnv *env, jclass,
                                                         jbyteArray nv21_, jbyteArray i420_,
                                                         jint w, jint h, jint orientation) {
    jbyte *nv21 = env->GetByteArrayElements(nv21_, JNI_FALSE);
    jbyte *i420 = env->GetByteArrayElements(i420_, JNI_FALSE);

    int ySize = w * h;
    int strideW = w >> 1;
    int strideH = h >> 1;
    int uSize = strideW * strideH;
    const uint8_t *nv21Y = reinterpret_cast<const uint8_t *>(nv21);
    const uint8_t *nv21VU = nv21Y + ySize;

    uint8_t *i420Y = reinterpret_cast<uint8_t *>(i420);
    uint8_t *i420U = i420Y + ySize;
    uint8_t *i420V = i420U + uSize;

    LOGD("nv21ToI420Rotate %d", orientation);//NV21与NV12顺序相反,因此U与V参数顺序相反
    switch (orientation) {
        case kRotate0:
            NV12ToI420Rotate(nv21Y, w, nv21VU, w, i420Y, w, i420V, strideW, i420U, strideW, w, h, kRotate0);
            break;
        case kRotate90:
            NV12ToI420Rotate(nv21Y, w, nv21VU, w, i420Y, h, i420V, strideH, i420U, strideH, w, h, kRotate90);
            break;
        case kRotate180:
            NV12ToI420Rotate(nv21Y, w, nv21VU, w, i420Y, w, i420V, strideW, i420U, strideW, w, h, kRotate180);
            break;
        case kRotate270:
            NV12ToI420Rotate(nv21Y, w, nv21VU, w, i420Y, h, i420V, strideH, i420U, strideH, w, h, kRotate270);
            break;
    }

    env->ReleaseByteArrayElements(nv21_, nv21, 0);
    env->ReleaseByteArrayElements(i420_, i420, 0);
}

/**
 * 同时NV21转I420裁剪旋转
 */
extern "C" JNIEXPORT void JNICALL
Java_you_chen_media_core_YuvUtils_clipNv21ToI420Rotate(JNIEnv *env, jclass,
                                                             jbyteArray nv21_, jbyteArray i420_,
                                                             jint w, jint h, jint cw, jint ch,
                                                             jint left, jint top, jint orientation) {
    jbyte *nv21 = env->GetByteArrayElements(nv21_, JNI_FALSE);
    jbyte *i420 = env->GetByteArrayElements(i420_, JNI_FALSE);

    const uint8_t *src = reinterpret_cast<const uint8_t *>(nv21);
    int srcSize = w * h * 3 / 2;
    int ySize = cw * ch;
    int strideW = cw >> 1;
    int strideH = ch >> 1;
    int uSize = strideW * strideH;

    uint8_t *i420Y = reinterpret_cast<uint8_t *>(i420);
    uint8_t *i420U = i420Y + ySize;
    uint8_t *i420V = i420U + uSize;

    LOGD("clipNv21ToI420Rotate %d", orientation);
    switch (orientation) {
        case kRotate0:
            ConvertToI420(src, srcSize, i420Y, cw, i420U, strideW, i420V, strideW,
                          left, top, w, h, cw, ch, kRotate0, FOURCC_NV21);
            break;
        case kRotate90:
            ConvertToI420(src, srcSize, i420Y, ch, i420U, strideH, i420V, strideH,
                          left, top, w, h, cw, ch, kRotate90, FOURCC_NV21);
            break;
        case kRotate180:
            ConvertToI420(src, srcSize, i420Y, cw, i420U, strideW, i420V, strideW,
                          left, top, w, h, cw, ch, kRotate180, FOURCC_NV21);
            break;
        case kRotate270:
            ConvertToI420(src, srcSize, i420Y, ch, i420U, strideH, i420V, strideH,
                          left, top, w, h, cw, ch, kRotate270, FOURCC_NV21);
            break;
    }

    env->ReleaseByteArrayElements(nv21_, nv21, 0);
    env->ReleaseByteArrayElements(i420_, i420, 0);
}

/**
 * 同时NV21转NV12旋转不裁剪方式
 */
extern "C" JNIEXPORT void JNICALL
Java_you_chen_media_core_YuvUtils_nv21ToNV12Rotate(JNIEnv *env, jclass,
                                                         jbyteArray nv21_, jbyteArray nv12_,
                                                         jint w, jint h, jint orientation) {
    jbyte *nv21 = env->GetByteArrayElements(nv21_, JNI_FALSE);
    jbyte *nv12 = env->GetByteArrayElements(nv12_, JNI_FALSE);

    int ySize = w * h;
    const uint8_t *nv21Y = reinterpret_cast<const uint8_t *>(nv21);
    const uint8_t *nv21VU = nv21Y + ySize;
    uint8_t *nv12Y = reinterpret_cast<uint8_t *>(nv12);
    uint8_t *nv12UV = nv12Y + ySize;

    LOGD("nv21ToNV12Rotate %d", orientation);
    switch (orientation) {
        case kRotate0:
            NV21ToNV12Rotate(nv21Y, w, nv21VU, w, nv12Y, w, nv12UV, w, w, h, kRotate0);
            break;
        case kRotate90:
            NV21ToNV12Rotate(nv21Y, w, nv21VU, w, nv12Y, h, nv12UV, h, w, h, kRotate90);
            break;
        case kRotate180:
            NV21ToNV12Rotate(nv21Y, w, nv21VU, w, nv12Y, w, nv12UV, w, w, h, kRotate180);
            break;
        case kRotate270:
            NV21ToNV12Rotate(nv21Y, w, nv21VU, w, nv12Y, h, nv12UV, h, w, h, kRotate270);
            break;
    }

    env->ReleaseByteArrayElements(nv21_, nv21, 0);
    env->ReleaseByteArrayElements(nv12_, nv12, 0);
}

/**
 * 同时NV21转NV12裁剪旋转
 */
extern "C" JNIEXPORT void JNICALL
Java_you_chen_media_core_YuvUtils_clipNv21ToNV12Rotate(JNIEnv *env, jclass,
                                                             jbyteArray nv21_, jbyteArray nv12_,
                                                             jint w, jint h, jint cw, jint ch,
                                                             jint left, jint top, jint orientation) {
    jbyte *nv21 = env->GetByteArrayElements(nv21_, JNI_FALSE);
    jbyte *nv12 = env->GetByteArrayElements(nv12_, JNI_FALSE);
    const uint8_t *nv21Y = reinterpret_cast<const uint8_t *>(nv21);

    uint8_t *nv12Y = reinterpret_cast<uint8_t *>(nv12);
    uint8_t *nv12UV = nv12Y + cw * ch;

    LOGD("clipNv21ToNV12Rotate %d", orientation);
    switch (orientation) {
        case kRotate0:
            ConvertNV21ToNV12(nv21Y, nv12Y, cw, nv12UV, cw, left, top, w, h, cw, ch, kRotate0);
            break;
        case kRotate90:
            ConvertNV21ToNV12(nv21Y, nv12Y, ch, nv12UV, ch, left, top, w, h, cw, ch, kRotate90);
            break;
        case kRotate180:
            ConvertNV21ToNV12(nv21Y, nv12Y, cw, nv12UV, cw, left, top, w, h, cw, ch, kRotate180);
            break;
        case kRotate270:
            ConvertNV21ToNV12(nv21Y, nv12Y, ch, nv12UV, ch, left, top, w, h, cw, ch, kRotate270);
            break;
    }

    env->ReleaseByteArrayElements(nv21_, nv21, 0);
    env->ReleaseByteArrayElements(nv12_, nv12, 0);
}

/**
 * 同时NV21转YV12旋转不裁剪方式
 */
extern "C" JNIEXPORT void JNICALL
Java_you_chen_media_core_YuvUtils_nv21ToYV12Rotate(JNIEnv *env, jclass,
                                                         jbyteArray nv21_, jbyteArray yv12_,
                                                         jint w, jint h, jint orientation) {
    jbyte *nv21 = env->GetByteArrayElements(nv21_, JNI_FALSE);
    jbyte *yv12 = env->GetByteArrayElements(yv12_, JNI_FALSE);

    int ySize = w * h;
    int strideW = w >> 1;
    int strideH = h >> 1;
    int uSize = strideW * strideH;
    const uint8_t *nv21Y = reinterpret_cast<const uint8_t *>(nv21);
    const uint8_t *nv21VU = nv21Y + ySize;

    uint8_t *yv12Y = reinterpret_cast<uint8_t *>(yv12);
    uint8_t *yv12U = yv12Y + ySize;
    uint8_t *yv12V = yv12U + uSize;

    LOGD("nv21ToYV12Rotate %d", orientation);//YV12与转I420的UV顺序相反即可
    switch (orientation) {
        case kRotate0:
            NV12ToI420Rotate(nv21Y, w, nv21VU, w, yv12Y, w, yv12U, strideW, yv12V, strideW, w, h, kRotate0);
            break;
        case kRotate90:
            NV12ToI420Rotate(nv21Y, w, nv21VU, w, yv12Y, h, yv12U, strideH, yv12V, strideH, w, h, kRotate90);
            break;
        case kRotate180:
            NV12ToI420Rotate(nv21Y, w, nv21VU, w, yv12Y, w, yv12U, strideW, yv12V, strideW, w, h, kRotate180);
            break;
        case kRotate270:
            NV12ToI420Rotate(nv21Y, w, nv21VU, w, yv12Y, h, yv12U, strideH, yv12V, strideH, w, h, kRotate270);
            break;
    }

    env->ReleaseByteArrayElements(nv21_, nv21, 0);
    env->ReleaseByteArrayElements(yv12_, yv12, 0);
}

/**
 * 同时NV21转YV12裁剪旋转
 */
extern "C" JNIEXPORT void JNICALL
Java_you_chen_media_core_YuvUtils_clipNv21ToYV12Rotate(JNIEnv *env, jclass,
                                                             jbyteArray nv21_, jbyteArray yv12_,
                                                             jint w, jint h, jint cw, jint ch,
                                                             jint left, jint top, jint orientation) {
    jbyte *nv21 = env->GetByteArrayElements(nv21_, JNI_FALSE);
    jbyte *yv12 = env->GetByteArrayElements(yv12_, JNI_FALSE);

    const uint8_t *src = reinterpret_cast<const uint8_t *>(nv21);
    int srcSize = w * h * 3 / 2;
    int ySize = cw * ch;
    int strideW = cw >> 1;
    int strideH = ch >> 1;
    int uSize = strideW * strideH;

    uint8_t *yv12Y = reinterpret_cast<uint8_t *>(yv12);
    uint8_t *yv12U = yv12Y + ySize;
    uint8_t *yv12V = yv12U + uSize;

    LOGD("clipNv21ToYV12Rotate %d", orientation);//YV12与转I420的UV顺序相反即可
    switch (orientation) {
        case kRotate0:
            ConvertToI420(src, srcSize, yv12Y, cw, yv12V, strideW, yv12U, strideW,
                          left, top, w, h, cw, ch, kRotate0, FOURCC_NV21);
            break;
        case kRotate90:
            ConvertToI420(src, srcSize, yv12Y, ch, yv12V, strideH, yv12U, strideH,
                          left, top, w, h, cw, ch, kRotate90, FOURCC_NV21);
            break;
        case kRotate180:
            ConvertToI420(src, srcSize, yv12Y, cw, yv12V, strideW, yv12U, strideW,
                          left, top, w, h, cw, ch, kRotate180, FOURCC_NV21);
            break;
        case kRotate270:
            ConvertToI420(src, srcSize, yv12Y, ch, yv12V, strideH, yv12U, strideH,
                          left, top, w, h, cw, ch, kRotate270, FOURCC_NV21);
            break;
    }

    env->ReleaseByteArrayElements(nv21_, nv21, 0);
    env->ReleaseByteArrayElements(yv12_, yv12, 0);
}

/**
 * NV21旋转
 */
extern "C" JNIEXPORT void JNICALL
Java_you_chen_media_core_YuvUtils_nv21Rotate(JNIEnv *env, jclass, jbyteArray nv21_,jbyteArray outs_,
                                                   jint w, jint h, jint orientation) {
    jbyte *nv21 = env->GetByteArrayElements(nv21_, JNI_FALSE);
    jbyte *outs = env->GetByteArrayElements(outs_, JNI_FALSE);

    int ySize = w * h;
    const uint8_t *nv21Y = reinterpret_cast<const uint8_t *>(nv21);
    const uint8_t *nv21VU = nv21Y + ySize;

    uint8_t *outsY = reinterpret_cast<uint8_t *>(outs);
    uint8_t *outsVU = outsY + ySize;

    LOGD("nv21Rotate %d", orientation);
    switch (orientation) {
        case kRotate0:
            memcpy(outsY, nv21Y, env->GetArrayLength(nv21_));//直接拷贝内存
            break;
        case kRotate90:
            NV21Rotate(nv21Y, w, nv21VU, w, outsY, h, outsVU, h, w, h, kRotate90);
            break;
        case kRotate180:
            NV21Rotate(nv21Y, w, nv21VU, w, outsY, w, outsVU, w, w, h, kRotate180);
            break;
        case kRotate270:
            NV21Rotate(nv21Y, w, nv21VU, w, outsY, h, outsVU, h, w, h, kRotate270);
            break;
    }

    env->ReleaseByteArrayElements(nv21_, nv21, 0);
    env->ReleaseByteArrayElements(outs_, outs, 0);
}

/**
 * 同时NV21裁剪旋转
 */
extern "C" JNIEXPORT void JNICALL
Java_you_chen_media_core_YuvUtils_clipNv21Rotate(JNIEnv *env, jclass,
                                                       jbyteArray nv21_, jbyteArray outs_,
                                                       jint w, jint h, jint cw, jint ch,
                                                       jint left, jint top, jint orientation) {
    jbyte *nv21 = env->GetByteArrayElements(nv21_, JNI_FALSE);
    jbyte *outs = env->GetByteArrayElements(outs_, JNI_FALSE);

    const uint8_t *nv21Y = reinterpret_cast<const uint8_t *>(nv21);
    uint8_t *outsY = reinterpret_cast<uint8_t *>(outs);
    uint8_t *outsVU = outsY + cw * ch;

    LOGD("clipNv21Rotate %d", orientation);
    switch (orientation) {
        case kRotate0:
            ConvertNV21(nv21Y, outsY, cw, outsVU, cw, left, top, w, h, cw, ch, kRotate0);
            break;
        case kRotate90:
            ConvertNV21(nv21Y, outsY, ch, outsVU, ch, left, top, w, h, cw, ch, kRotate90);
            break;
        case kRotate180:
            ConvertNV21(nv21Y, outsY, cw, outsVU, cw, left, top, w, h, cw, ch, kRotate180);
            break;
        case kRotate270:
            ConvertNV21(nv21Y, outsY, ch, outsVU, ch, left, top, w, h, cw, ch, kRotate270);
            break;
    }

    env->ReleaseByteArrayElements(nv21_, nv21, 0);
    env->ReleaseByteArrayElements(outs_, outs, 0);
}