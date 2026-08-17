#pragma once

#include <cstdint>
#include <string>
#include "StreamType.h"

struct StreamInfo {
    int index = -1;

    StreamType type = StreamType::Unknown;

    std::string codec;

    int codecId = 0;

    int64_t bitrate = 0;

    //
    // Video
    //
    int width = 0;

    int height = 0;

    double fps = 0.0;

    //
    // Audio
    //
    int sampleRate = 0;
    int channels = 0;
};
