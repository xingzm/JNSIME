#define LOG_TAG "JNIBlueoceanAccelerationManager"
#include "jni.h"
#include "com_blueocean_ime_BlueoceanAccelerationManager.h"
#include <linux/input.h>
#include <stdlib.h>
#include <stdio.h>
#include <pthread.h>
#include <time.h>
#include <fcntl.h>
#include <errno.h>
#include <dirent.h>
#include <linux/input.h>
#include <sys/ioctl.h>
#include <cutils/log.h>
#include <android/log.h>
#include "ioctl.h"

JNIEXPORT jint JNICALL
Java_com_blueocean_ime_BlueoceanAccelerationManager_openDevice(JNIEnv *env, jclass jzz) {
	int fd = open("/dev/sensor_acc", O_RDWR);
	return fd;
}

JNIEXPORT jboolean JNICALL
Java_com_blueocean_ime_BlueoceanAccelerationManager_setInstallDirection(JNIEnv *env, jclass jzz, jint fd, jint mInstallDir) {
	if (ioctl(fd, SENSOR_IOC_SET_INSTALL_DIR, &mInstallDir)) {
		LOGE("Error: set acceleration sensor install direction = %d error", mInstallDir);	
		return JNI_FALSE;
	}
	return JNI_TRUE;
}

JNIEXPORT jboolean JNICALL
Java_com_blueocean_ime_BlueoceanAccelerationManager_setDelay(JNIEnv *env, jclass jzz, jint fd, jint mDelay) {
        if (ioctl(fd, SENSOR_IOC_SET_DELAY, &mDelay)) {
                LOGE("Error: set acceleration sensor delay = %d error", mDelay);
                return JNI_FALSE;
        }
        return JNI_TRUE;
}

JNIEXPORT jboolean JNICALL
Java_com_blueocean_ime_BlueoceanAccelerationManager_setData(JNIEnv *env, jclass jzz, jint fd, jintArray data) {
	jint *acc_vec;
	jboolean bret = JNI_FALSE;
	acc_vec = env->GetIntArrayElements(data, false);
	if (acc_vec != NULL) {
		if (ioctl(fd, SENSOR_IOC_SET_DATA, acc_vec)) {
			LOGE("Error: set acceleration sensor dadta[0] = %d data[1] = %d data[2] = %d error", 
				acc_vec[0], acc_vec[1], acc_vec[2]);
		} else bret = JNI_TRUE;
	}
	env->ReleaseIntArrayElements(data, acc_vec, 0);
	return bret;
}

JNIEXPORT void JNICALL
Java_com_blueocean_ime_BlueoceanAccelerationManager_closeDevice(JNIEnv * env, jclass jzz, jint fd) {
	close(fd);
}
