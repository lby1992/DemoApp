#include <jni.h>
#include <android/log.h>
#include "JniCache.h"
#include "JniMapper.h"
#include "ffmpeg/FFmpegProbe.h"
#include "ffmpeg/FFmpegDecoder.h"
#include "player/NativePlayer.h"

#define LOG_TAG "JNI"

#define LOGV(...) __android_log_print(ANDROID_LOG_VERBOSE, LOG_TAG, __VA_ARGS__)
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define LOGW(...) __android_log_print(ANDROID_LOG_WARN, LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

JNIEXPORT jint JNICALL
JNI_OnLoad(
        JavaVM *vm,
        void *
) {
    JNIEnv *env = nullptr;

    if (
            vm->GetEnv(
                    reinterpret_cast<void **>(&env),
                    JNI_VERSION_1_6
            )
                    != JNI_OK
            ) {
        return JNI_ERR;
    }

    if (!JniCache::init(env)) {
        return JNI_ERR;
    }

    return JNI_VERSION_1_6;
}
//
//extern "C"
//JNIEXPORT jlong JNICALL
//Java_dev_dl_demoapp_core_jni_NativeLib_getFFmpegVersion(JNIEnv *env, jobject thiz) {
//    return avformat_version();
//}

extern "C"
JNIEXPORT jobject JNICALL
Java_dev_dl_demoapp_core_jni_NativeLib_probeMedia(JNIEnv *env, jobject thiz, jstring url_) {
    const char *url = env->GetStringUTFChars(url_, nullptr);

    FFmpegProbe probe;

    Result<MediaInfo> result = probe.probe(url);

    env->ReleaseStringUTFChars(url_, url);

    return JniMapper::toJava(env, result);
}

extern "C"
JNIEXPORT jobject JNICALL
Java_dev_dl_demoapp_core_jni_NativeLib_testDecoderOpen(JNIEnv *env, jobject thiz, jstring url_) {
    const char *url = env->GetStringUTFChars(url_, nullptr);

    FFmpegDecoder decoder;

    auto result = decoder.open(url);

    env->ReleaseStringUTFChars(url_, url);

    return JniMapper::createNativeResult(env, result.success, result.errorCode, result.errorMessage);
}