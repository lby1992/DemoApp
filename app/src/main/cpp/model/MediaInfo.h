#pragma once

#include <cstdint>
#include <vector>

#include "StreamInfo.h"

struct MediaInfo
{
    int64_t durationMs;

    std::vector<StreamInfo> streams;
};
