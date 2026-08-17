//
// Created by libiy on 7/11/2026.
//

#include "JniMapper.h"
#include "JniCache.h"

#include <vector>

jobject JniMapper::createNativeResult(
        JNIEnv *env,
        bool success,
        int errorCode,
        const std::string &message
) {

    jobject obj = env->NewObject(JniCache::nativeResultClass,JniCache::nativeResultConstructor);
    env->SetBooleanField(obj,JniCache::nativeResultSuccess,success);
    env->SetIntField(obj,JniCache::nativeResultErrorCode,errorCode);
    jstring error =env->NewStringUTF(message.c_str());
    env->SetObjectField(obj,JniCache::nativeResultErrorMessage,error);
    env->DeleteLocalRef(error);

    return obj;
}

jobject JniMapper::toJava(JNIEnv *env, const Result<MediaInfo> &result) {
    jobject obj = createNativeResult(env, result.success, result.errorCode, result.errorMessage);

    if (result.success) {
        env->SetObjectField(obj, JniCache::nativeResultData, createMediaInfo(env, result.data));
    }

    return obj;
}

jobject JniMapper::createMediaInfo(JNIEnv *env, const MediaInfo &media) {
    jobject obj = env->NewObject(JniCache::mediaInfoClass, JniCache::mediaInfoConstructor);

    env->SetLongField(obj, JniCache::mediaInfoDuration, media.durationMs);

    jobject streamList = env->NewObject(JniCache::arrayListClass, JniCache::arrayListConstructor);

    for (const auto &stream: media.streams) {
        env->CallBooleanMethod(streamList, JniCache::arrayListAdd, createStreamInfo(env, stream));
    }

    env->SetObjectField(obj, JniCache::mediaInfoStreams, streamList);

    return obj;
}

jobject JniMapper::createStreamInfo(JNIEnv *env, const StreamInfo &stream) {
    jobject obj = env->NewObject(JniCache::streamInfoClass, JniCache::streamInfoConstructor);

    env->SetIntField(obj, JniCache::streamInfoIndex, stream.index);
    env->SetIntField(obj, JniCache::streamInfoType, static_cast<jint>(stream.type));
    jstring codec = env->NewStringUTF(stream.codec.c_str());
    env->SetObjectField(obj, JniCache::streamInfoCodec, codec);
    env->SetIntField(obj, JniCache::streamInfoCodecId, stream.codecId);
    env->SetIntField(obj, JniCache::streamInfoWidth, stream.width);
    env->SetIntField(obj, JniCache::streamInfoHeight, stream.height);
    env->SetDoubleField(obj, JniCache::streamInfoFps, stream.fps);
    env->SetLongField(obj, JniCache::streamInfoBitrate, stream.bitrate);
    env->SetIntField(obj, JniCache::streamInfoSampleRate, stream.sampleRate);
    env->SetIntField(obj, JniCache::streamInfoChannels, stream.channels);

    env->DeleteLocalRef(codec);

    return obj;
}
