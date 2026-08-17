//
// Created by libiy on 7/11/2026.
//

#pragma once

#include <jni.h>

#include "model/Result.h"
#include "model/MediaInfo.h"


class JniMapper {

public:
    static jobject createNativeResult(
            JNIEnv *env,
            bool success,
            int errorCode,
            const std::string &message
    );

    static jobject toJava(
            JNIEnv *env,
            const Result<MediaInfo> &result

    );

private:
    static jobject createMediaInfo(
            JNIEnv *env,
            const MediaInfo &media
    );

    static jobject createStreamInfo(
            JNIEnv *env,
            const StreamInfo &stream
    );

};
