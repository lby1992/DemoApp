#pragma once

#include <cstdint>

struct FrameInfo {
    int width = 0;

    int height = 0;

    // AVPixelFormat
    int pixelFormat = 0;

    // presentation timestamp
    int64_t pts = 0;

    int64_t duration = 0;
};