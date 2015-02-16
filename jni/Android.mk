
LOCAL_PATH := $(call my-dir)
 
include $(CLEAR_VARS)
 
LOCAL_LDLIBS := -llog
 
LOCAL_MODULE    := myReceiptCap
LOCAL_SRC_FILES := ReceiptCap.c
 
include $(BUILD_SHARED_LIBRARY)