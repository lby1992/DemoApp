#pragma once

#include <jni.h>

class JniCache {
public:
    static bool init(
            JNIEnv *env
    );

    static void destroy(JNIEnv* env);

    // NativeResult
    static jclass nativeResultClass;
    static jmethodID nativeResultConstructor;
    static jfieldID nativeResultSuccess;
    static jfieldID nativeResultErrorCode;
    static jfieldID nativeResultErrorMessage;
    static jfieldID nativeResultData;

    // MediaInfo
    static jclass mediaInfoClass;
    static jmethodID mediaInfoConstructor;
    static jfieldID mediaInfoDuration;
    static jfieldID mediaInfoStreams;

    // StreamInfo
    static jclass streamInfoClass;
    static jmethodID streamInfoConstructor;
    static jfieldID streamInfoIndex;
    static jfieldID streamInfoType;
    static jfieldID streamInfoCodec;
    static jfieldID streamInfoCodecId;
    static jfieldID streamInfoWidth;
    static jfieldID streamInfoHeight;
    static jfieldID streamInfoFps;
    static jfieldID streamInfoBitrate;
    static jfieldID streamInfoSampleRate;
    static jfieldID streamInfoChannels;


    // ArrayList
    static jclass arrayListClass;
    static jmethodID arrayListConstructor;
    static jmethodID arrayListAdd;
};