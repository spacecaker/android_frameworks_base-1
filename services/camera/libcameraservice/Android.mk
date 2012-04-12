LOCAL_PATH:= $(call my-dir)

#
# libcameraservice
#

include $(CLEAR_VARS)

LOCAL_SRC_FILES:=               \
    CameraService.cpp

LOCAL_SHARED_LIBRARIES:= \
    libui \
    libutils \
    libbinder \
    libcutils \
    libmedia \
    libcamera_client \
    libgui \
    libhardware

LOCAL_MODULE:= libcameraservice

ifeq ($(BOARD_HAVE_HTC_FFC), true)
LOCAL_CFLAGS += -DBOARD_HAVE_HTC_FFC
endif

ifeq ($(BOARD_CAMERA_NO_AUTOFOCUS),true)
LOCAL_CFLAGS += -DBOARD_CAMERA_NO_AUTOFOCUS
endif
include $(BUILD_SHARED_LIBRARY)
