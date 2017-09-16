LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)


LOCAL_JPEG_PATH := ./jpeg-8c

LOCAL_JPEG_SRC_FILES := 		\
                   $(LOCAL_JPEG_PATH)/jaricom.c             \
                   $(LOCAL_JPEG_PATH)/jcapimin.c             \
                   $(LOCAL_JPEG_PATH)/jcapistd.c             \
                   $(LOCAL_JPEG_PATH)/jcarith.c             \
                   $(LOCAL_JPEG_PATH)/jccoefct.c             \
                   $(LOCAL_JPEG_PATH)/jccolor.c             \
                   $(LOCAL_JPEG_PATH)/jcdctmgr.c             \
                   $(LOCAL_JPEG_PATH)/jchuff.c             \
                   $(LOCAL_JPEG_PATH)/jcinit.c             \
                   $(LOCAL_JPEG_PATH)/jcmainct.c             \
                   $(LOCAL_JPEG_PATH)/jcmarker.c             \
                   $(LOCAL_JPEG_PATH)/jcmaster.c             \
                   $(LOCAL_JPEG_PATH)/jcomapi.c             \
                   $(LOCAL_JPEG_PATH)/jcparam.c             \
                   $(LOCAL_JPEG_PATH)/jcprepct.c             \
                   $(LOCAL_JPEG_PATH)/jcsample.c             \
                   $(LOCAL_JPEG_PATH)/jctrans.c             \
                   $(LOCAL_JPEG_PATH)/jdapimin.c             \
                   $(LOCAL_JPEG_PATH)/jdapistd.c             \
                   $(LOCAL_JPEG_PATH)/jdarith.c             \
                   $(LOCAL_JPEG_PATH)/jdatadst.c             \
                   $(LOCAL_JPEG_PATH)/jdatasrc.c             \
                   $(LOCAL_JPEG_PATH)/jdcoefct.c             \
                   $(LOCAL_JPEG_PATH)/jdcolor.c             \
                   $(LOCAL_JPEG_PATH)/jddctmgr.c             \
                   $(LOCAL_JPEG_PATH)/jdhuff.c             \
                   $(LOCAL_JPEG_PATH)/jdinput.c             \
                   $(LOCAL_JPEG_PATH)/jdmainct.c             \
                   $(LOCAL_JPEG_PATH)/jdmarker.c             \
                   $(LOCAL_JPEG_PATH)/jdmaster.c             \
                   $(LOCAL_JPEG_PATH)/jdmerge.c             \
                   $(LOCAL_JPEG_PATH)/jdpostct.c             \
                   $(LOCAL_JPEG_PATH)/jdsample.c             \
                   $(LOCAL_JPEG_PATH)/jdtrans.c             \
                   $(LOCAL_JPEG_PATH)/jerror.c             \
                   $(LOCAL_JPEG_PATH)/jfdctflt.c             \
                   $(LOCAL_JPEG_PATH)/jfdctfst.c             \
                   $(LOCAL_JPEG_PATH)/jfdctint.c             \
                   $(LOCAL_JPEG_PATH)/jidctflt.c             \
                   $(LOCAL_JPEG_PATH)/jidctfst.c             \
                   $(LOCAL_JPEG_PATH)/jidctint.c             \
                   $(LOCAL_JPEG_PATH)/jquant1.c             \
                   $(LOCAL_JPEG_PATH)/jquant2.c             \
                   $(LOCAL_JPEG_PATH)/jutils.c             \
                   $(LOCAL_JPEG_PATH)/jmemmgr.c             \
                   $(LOCAL_JPEG_PATH)/jmemnobs.c             \
                   $(LOCAL_JPEG_PATH)/transupp.c

###########################################################

LOCAL_SRC_FILES:= $(LOCAL_JPEG_SRC_FILES)
LOCAL_C_INCLUDES += $(LOCAL_JPEG_PATH)
LOCAL_CFLAGS += -Wl, -rdynamic
LOCAL_MODULE := libjpeg-8c
include $(BUILD_SHARED_LIBRARY)


include $(CLEAR_VARS)
LOCAL_MODULE := eprinterdriver-prebuilt
LOCAL_SRC_FILES := ../libs/libeprinterdriver.so
include $(PREBUILT_SHARED_LIBRARY)
LOCAL_SHARED_LIBRARY := eprinterdriver-prebuilt
LOCAL_LDLIBS = -L$(PROJECT_PATH)/libs/$(TARGET_ARCH_ABI)/ -leprinterdriver


