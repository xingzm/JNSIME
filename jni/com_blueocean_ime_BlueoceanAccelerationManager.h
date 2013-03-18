#include "jni.h"
#include <android/log.h>

#ifndef _Included_com_blueocean_ime_BlueoceanAccelerationManager
#define _Included_com_blueocean_ine_BlueoceanAccelerationManager

#ifdef __cplusplus
extern "C" {
#endif

#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR  , LOG_TAG, __VA_ARGS__)

JNIEXPORT jint JNICALL
Java_com_blueocean_ime_BlueoceanAccelerationManager_openDevice(JNIEnv *env, jclass jzz);

JNIEXPORT jboolean JNICALL
Java_com_blueocean_ime_BlueoceanAccelerationManager_setInstallDirection(JNIEnv *env, jclass jzz, jint fd, jint mInstallDir);

JNIEXPORT jboolean JNICALL
Java_com_blueocean_ime_BlueoceanAccelerationManager_setDelay(JNIEnv *env, jclass jzz, jint fd, jint mDelay);

JNIEXPORT jboolean JNICALL
Java_com_blueocean_ime_BlueoceanAccelerationManager_setData(JNIEnv *env, jclass jzz, jint fd, jintArray data);

JNIEXPORT void JNICALL
Java_com_blueocean_ime_BlueoceanAccelerationManager_closeDevice(JNIEnv * env, jclass jzz, jint fd);

#ifdef __cplusplus
}
#endif
#endif
