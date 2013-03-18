#define LOG_TAG "JNIBlueoceanMotionManager"
#include "com_blueocean_ime_BlueoceanMotionManager.h"
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
 
static struct jnsime_touch_panel_data data_event;

JNIEXPORT jint JNICALL
Java_com_blueocean_ime_BlueoceanMotionManager_openDevice(JNIEnv *env, jclass jzz) {
	int fd = open("/dev/jnsime_touch_panel" , O_RDWR);
	LOGE("Open /dev/jnsime_touch_panel fd = %d", fd);
	return fd;
}

JNIEXPORT void JNICALL
Java_com_blueocean_ime_BlueoceanMotionManager_injectMotionEventDown(JNIEnv *env, jclass jzz, jint fd, jint finger_id, jfloat x, jfloat y) {
	data_event.state = finger_id;
	data_event.x[finger_id - 1] = (int)x;
	data_event.y[finger_id - 1] = (int)y;
	data_event.touch_major = 1;
	data_event.width_major = 1;
	if (ioctl(fd, JNSIME_IOCTL_TOUCH_DOWN, &data_event)) {
		LOGE("Error: jnsime touch panel data down");
	}
}

JNIEXPORT void JNICALL
Java_com_blueocean_ime_BlueoceanMotionManager_injectMotionEventUp(JNIEnv *env, jclass jzz, jint fd, jint finger_id) {
	data_event.state = finger_id;
	data_event.x[0] = 0;
	data_event.y[0] = 0;
	data_event.x[1] = 0;
	data_event.y[1] = 0;
	data_event.touch_major = 0;
	data_event.width_major = 0;
	if (ioctl(fd, JNSIME_IOCTL_TOUCH_UP, &data_event)) {
		LOGE("Error: jnsime data up");
		return;
	}
}

JNIEXPORT void JNICALL
Java_com_blueocean_ime_BlueoceanMotionManager_closeDevice(JNIEnv * env, jclass jzz, jint fd) {
	close(fd);
}
