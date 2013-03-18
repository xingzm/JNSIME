#include "jni.h"
#include "android/log.h"
#ifndef _Included_com_blueocean_ime_BlueoceanKeyManager
#define _Included_com_blueocean_ine_BlueoceanKeyManager

#ifdef __cplusplus
extern "C" {
#endif

#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR  , LOG_TAG, __VA_ARGS__)

JNIEXPORT jboolean JNICALL
Java_com_blueocean_ime_BlueoceanKeyManager_createKeyThread(JNIEnv *env, jclass jzz);

JNIEXPORT void JNICALL
Java_com_blueocean_ime_BlueoceanKeyManager_getKey(JNIEnv *env, jclass jzz, jobject keyEvent);

JNIEXPORT jboolean JNICALL
Java_com_blueocean_ime_BlueoceanKeyManager_configTouchpanelPos(JNIEnv *env, jclass jzz, jint x, jint y);

JNIEXPORT void JNICALL
Java_com_blueocean_ime_BlueoceanKeyManager_closeDevices(JNIEnv * env, jclass jzz);

#ifdef __cplusplus
}
#endif
#endif
