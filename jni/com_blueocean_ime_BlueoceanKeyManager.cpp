#define LOG_TAG "JNIBlueoceanKeyManager"
  
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
#include "com_blueocean_ime_BlueoceanKeyManager.h"
#include <android/log.h>
#include <EventHub.h>
#include "ioctl.h"
 
//#define LOGI(...) do { __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__); } while(0)
//#define LOGW(...) do { __android_log_print(ANDROID_LOG_WARN, LOG_TAG, __VA_ARGS__); } while(0)
//#define LOGE(...) do { __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__); } while(0)  

#define ABS_MT_POSITION_X       0x35    /* Center X ellipse position */
#define ABS_MT_POSITION_Y	0x36
using namespace android;
static bool debug = true;
struct keyManager {
	struct key_event *event;
	int fd;
};

struct keyManager *keyManager;

static int openInput(const char* inputName) {
    int fd = -1;
    const char *dirname = "/dev/input";
    char devname[PATH_MAX];
    char *filename;
    DIR *dir;
    struct dirent *de;
    dir = opendir(dirname);
    if(dir == NULL)
        return -1;
    strcpy(devname, dirname);
    filename = devname + strlen(devname);
    *filename++ = '/';
    while((de = readdir(dir))) {
        if(de->d_name[0] == '.' &&
                (de->d_name[1] == '\0' ||
                        (de->d_name[1] == '.' && de->d_name[2] == '\0')))
            continue;
        strcpy(filename, de->d_name);
        fd = open(devname, O_RDWR);
        if (fd>=0) {
            char name[80];
            if (ioctl(fd, EVIOCGNAME(sizeof(name) - 1), &name) < 1) {
                name[0] = '\0';
            }
            if (!strcmp(name, inputName)) {
		LOGE("Found %s input event", inputName);
		return fd;
	    } else {
                close(fd);
                fd = -1;
            }
        }
    }
    closedir(dir);
    return -1;
}

void *runableKey(void *) {
	while (true) {
		if (ioctl(keyManager->fd, KEY_MANAGER_GET_EVENT, keyManager->event)) {
			LOGE("get key_event error");
		}
		usleep(50 * 1000);
	}
	return 0;
}

JNIEXPORT jboolean JNICALL
Java_com_blueocean_ime_BlueoceanKeyManager_createKeyThread(JNIEnv *env, jclass jzz) {
	pthread_t tid;
	keyManager = (struct keyManager*)calloc(sizeof(char), sizeof(struct keyManager));
	if (!keyManager) {
		LOGE("calloc keyManager memory error");
		return JNI_FALSE;
	}
	keyManager->event = (struct key_event*)calloc(sizeof(char), sizeof(struct key_event));
	if (!keyManager->event) {
		LOGE("calloc keyManager->event [struct key_event] error");
		return JNI_FALSE;
	}
	keyManager->fd = open("/dev/key_manager", O_RDWR);
        if (keyManager->fd <= 0) {
                LOGE("Open /dev/key_manager device error\n");
                return JNI_FALSE;
        }
	LOGE("Open /dev/key_manager device fd = %d", keyManager->fd);
	pthread_create(&tid, NULL, runableKey, NULL);
	return JNI_TRUE;
}

JNIEXPORT void JNICALL
Java_com_blueocean_ime_BlueoceanKeyManager_getKey(JNIEnv *env, jclass jzz, jobject keyEvent) {
	jclass objectClass = env->FindClass("com/blueocean/ime/BlueoceanKeyEvent");
	jfieldID type = env->GetFieldID(objectClass, "type", "I");
	jfieldID scanCode = env->GetFieldID(objectClass, "scanCode", "I");
	jfieldID keyCode = env->GetFieldID(objectClass, "keyCode", "I");
	jfieldID value = env->GetFieldID(objectClass, "value", "I");

	env->SetIntField(keyEvent, type, keyManager->event->type);
	env->SetIntField(keyEvent, scanCode, keyManager->event->scan_code);
	env->SetIntField(keyEvent, keyCode, keyManager->event->key_code);
	env->SetIntField(keyEvent, value, keyManager->event->value);
}

JNIEXPORT jboolean JNICALL
Java_com_blueocean_ime_BlueoceanKeyManager_configTouchpanelPos(JNIEnv *env, jclass jzz, jint x, jint y) {
	struct input_event tpEvent;
	tpEvent.type = EV_ABS;
	tpEvent.code = ABS_MT_POSITION_X;
	tpEvent.value = x;
	return JNI_TRUE;
}

JNIEXPORT void JNICALL
Java_com_blueocean_ime_BlueoceanKeyManager_closeDevices(JNIEnv * env, jclass jzz) {
	close(keyManager->fd);	
}
