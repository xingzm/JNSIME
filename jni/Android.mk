LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)
LOCAL_MODULE_TAGS := eng
LOCAL_MODULE:=libjnskey
LOCAL_SRC_FILES:= com_blueocean_ime_BlueoceanKeyManager.cpp
#LOCAL_SHARED_LIBRARIES := liblog libui
base = $(LOCAL_PATH)/../../../../frameworks/base

LOCAL_C_INCLUDES := \
        $(base)/services/camera/libcameraservice \
        $(base)/services/audioflinger \
        $(base)/services/surfaceflinger \
        $(base)/services/sensorservice \
        $(base)/media/libmediaplayerservice \
	$(base)/services/input \
        $(JNI_H_INCLUDE)

LOCAL_SHARED_LIBRARIES := \
        libandroid_runtime \
        libsensorservice \
        libsurfaceflinger \
        libaudioflinger \
    libcameraservice \
    libmediaplayerservice \
    libinput \
        libutils \
        libbinder \
        libcutils
LOCAL_LDLIBS := -ldl -llog
LOCAL_STATIC_LIBRARIES :=
LOCAL_C_INCLUDES += \
	$(JNI_H_INCLUDE)
LOCAL_PRELINK_MODULE := false
include $(BUILD_SHARED_LIBRARY)
#############################################################
include $(CLEAR_VARS)
LOCAL_MODULE_TAGS := eng
LOCAL_MODULE:=libjnsmotion
LOCAL_SRC_FILES:= com_blueocean_ime_BlueoceanMotionManager.cpp
#LOCAL_SHARED_LIBRARIES := liblog libui
LOCAL_SHARED_LIBRARIES := \
    libandroid_runtime \
        libcutils \
        libhardware \
        libhardware_legacy \
        libnativehelper \
    libsystem_server \
        libutils \
        libui 
#  libsurfaceflinger_client
LOCAL_LDLIBS := -ldl -llog
LOCAL_STATIC_LIBRARIES :=
LOCAL_C_INCLUDES += \
        $(JNI_H_INCLUDE)
LOCAL_PRELINK_MODULE := false
include $(BUILD_SHARED_LIBRARY)
##############################################################
include $(CLEAR_VARS)
LOCAL_MODULE_TAGS := eng
LOCAL_MODULE:=libjnsacc
LOCAL_SRC_FILES:= com_blueocean_ime_BlueoceanAccelerationManager.cpp
#LOCAL_SHARED_LIBRARIES := liblog libui
LOCAL_SHARED_LIBRARIES := \
    libandroid_runtime \
        libcutils \
        libhardware \
        libhardware_legacy \
        libnativehelper \
    libsystem_server \
        libutils \
        libui 
#    libsurfaceflinger_client
LOCAL_LDLIBS := -ldl -llog
LOCAL_STATIC_LIBRARIES :=
LOCAL_C_INCLUDES += \
        $(JNI_H_INCLUDE)
LOCAL_PRELINK_MODULE := false
include $(BUILD_SHARED_LIBRARY)
