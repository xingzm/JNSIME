#include "jni.h"
#include <android/log.h>

#ifndef _Included_com_blueocean_ime_BlueoceanMotionManager
#define _Included_com_blueocean_ine_BlueoceanMotionManager

#ifdef __cplusplus
extern "C" {
#endif

#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR  , LOG_TAG, __VA_ARGS__)

JNIEXPORT jint JNICALL
Java_com_blueocean_ime_BlueoceanMotionManager_openDevice(JNIEnv *env, jclass jzz);

JNIEXPORT void JNICALL
Java_com_blueocean_ime_BlueoceanMotionManager_injectMotionEventDown(JNIEnv *env, jclass jzz, jint fd, jfloat x, jfloat y);

JNIEXPORT void JNICALL
Java_com_blueocean_ime_BlueoceanMotionManager_injectMotionEventUp(JNIEnv *env, jclass jzz, jint fd);

JNIEXPORT void JNICALL
Java_com_blueocean_ime_BlueoceanMotionManager_closeDevice(JNIEnv * env, jclass jzz, jint fd);

#ifdef __cplusplus
}
#endif
#endif
