LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE    := trippingImageMogrifier
LOCAL_SRC_FILES := holamundo.c

include $(BUILD_SHARED_LIBRARY)
