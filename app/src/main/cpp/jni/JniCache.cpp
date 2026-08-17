#include "JniCache.h"


jclass JniCache::nativeResultClass = nullptr;

jmethodID JniCache::nativeResultConstructor = nullptr;

jfieldID JniCache::nativeResultSuccess = nullptr;

jfieldID JniCache::nativeResultErrorCode = nullptr;

jfieldID JniCache::nativeResultErrorMessage = nullptr;

jfieldID JniCache::nativeResultData = nullptr;


jclass JniCache::mediaInfoClass = nullptr;

jmethodID JniCache::mediaInfoConstructor = nullptr;

jfieldID JniCache::mediaInfoDuration = nullptr;

jfieldID JniCache::mediaInfoStreams = nullptr;


jclass JniCache::streamInfoClass = nullptr;

jmethodID JniCache::streamInfoConstructor = nullptr;

jfieldID JniCache::streamInfoIndex = nullptr;

jfieldID JniCache::streamInfoType = nullptr;

jfieldID JniCache::streamInfoCodec = nullptr;

jfieldID JniCache::streamInfoCodecId = nullptr;

jfieldID JniCache::streamInfoWidth = nullptr;

jfieldID JniCache::streamInfoHeight = nullptr;

jfieldID JniCache::streamInfoFps = nullptr;

jfieldID JniCache::streamInfoBitrate = nullptr;

jfieldID JniCache::streamInfoSampleRate = nullptr;

jfieldID JniCache::streamInfoChannels = nullptr;


jclass JniCache::arrayListClass = nullptr;

jmethodID JniCache::arrayListConstructor = nullptr;

jmethodID JniCache::arrayListAdd = nullptr;

bool JniCache::init(JNIEnv *env) {
    jclass cls;


    //
    // NativeResult
    //

    cls =
            env->FindClass(
                    "dev/dl/demoapp/core/jni/NativeResult"
            );


    if (cls == nullptr)
        return false;


    nativeResultClass =
            reinterpret_cast<jclass>(
                    env->NewGlobalRef(cls)
            );


    nativeResultConstructor =
            env->GetMethodID(
                    cls,
                    "<init>",
                    "()V"
            );


    nativeResultSuccess =
            env->GetFieldID(
                    cls,
                    "success",
                    "Z"
            );


    nativeResultErrorCode =
            env->GetFieldID(
                    cls,
                    "errorCode",
                    "I"
            );


    nativeResultErrorMessage =
            env->GetFieldID(
                    cls,
                    "errorMessage",
                    "Ljava/lang/String;"
            );


    nativeResultData =
            env->GetFieldID(
                    cls,
                    "data",
                    "Ljava/lang/Object;"
            );

    //
    // MediaInfo
    //

    cls = env->FindClass("dev/dl/demoapp/core/jni/MediaInfo");

    if (cls == nullptr)
        return false;


    mediaInfoClass =
            static_cast<jclass>(
                    env->NewGlobalRef(cls)
            );


    mediaInfoConstructor =
            env->GetMethodID(
                    cls,
                    "<init>",
                    "()V"
            );


    mediaInfoDuration =
            env->GetFieldID(
                    cls,
                    "durationMs",
                    "J"
            );


    mediaInfoStreams =
            env->GetFieldID(
                    cls,
                    "streams",
                    "Ljava/util/List;"
            );

    //
    // StreamInfo
    //

    cls =
            env->FindClass(
                    "dev/dl/demoapp/core/jni/StreamInfo"
            );


    if(cls == nullptr)
        return false;


    streamInfoClass =
            static_cast<jclass>(
                    env->NewGlobalRef(cls)
            );


    streamInfoConstructor =
            env->GetMethodID(
                    cls,
                    "<init>",
                    "()V"
            );


    streamInfoIndex =
            env->GetFieldID(
                    cls,
                    "index",
                    "I"
            );


    streamInfoType =
            env->GetFieldID(
                    cls,
                    "type",
                    "I"
            );


    streamInfoCodec =
            env->GetFieldID(
                    cls,
                    "codec",
                    "Ljava/lang/String;"
            );


    streamInfoCodecId =
            env->GetFieldID(
                    cls,
                    "codecId",
                    "I"
            );


    streamInfoWidth =
            env->GetFieldID(
                    cls,
                    "width",
                    "I"
            );


    streamInfoHeight =
            env->GetFieldID(
                    cls,
                    "height",
                    "I"
            );


    streamInfoFps =
            env->GetFieldID(
                    cls,
                    "fps",
                    "D"
            );


    streamInfoBitrate =
            env->GetFieldID(
                    cls,
                    "bitrate",
                    "J"
            );


    streamInfoSampleRate =
            env->GetFieldID(
                    cls,
                    "sampleRate",
                    "I"
            );


    streamInfoChannels =
            env->GetFieldID(
                    cls,
                    "channels",
                    "I"
            );


    //
    // ArrayList
    //

    cls =
            env->FindClass(
                    "java/util/ArrayList"
            );


    if(cls == nullptr)
        return false;


    arrayListClass =
            static_cast<jclass>(
                    env->NewGlobalRef(cls)
            );


    arrayListConstructor =
            env->GetMethodID(
                    cls,
                    "<init>",
                    "()V"
            );


    arrayListAdd =
            env->GetMethodID(
                    cls,
                    "add",
                    "(Ljava/lang/Object;)Z"
            );



    return true;
}

void JniCache::destroy(JNIEnv* env) {
    env->DeleteGlobalRef(nativeResultClass);
    env->DeleteGlobalRef(mediaInfoClass);
    env->DeleteGlobalRef(streamInfoClass);
    env->DeleteGlobalRef(arrayListClass);
}
