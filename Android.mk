LOCAL_PATH:= $(call my-dir)
###############################################################################
include $(CLEAR_VARS)
LOCAL_MODULE_TAGS := optional
LOCAL_PROGUARD_ENABLED := full
#LOCAL_PROGUARD_FLAG_FILES := proguard.flag1
#LOCAL_STATIC_JAVA_LIBRARIES := libarity

LOCAL_SRC_FILES := $(call all-java-files-under, src)

LOCAL_PACKAGE_NAME := JNSIME
#LOCAL_SDK_VERSION := current
LOCAL_CERTIFICATE := platform
LOCAL_JNI_SHARED_LIBRARIES := libjnskey libjnsmotion libjnsacc
include $(BUILD_PACKAGE)
# Use the folloing include to make our test apk.
include $(call all-makefiles-under,$(LOCAL_PATH))


