#pragma once

#include "model/Result.h"
#include "model/MediaInfo.h"

class FFmpegProbe {
public:
    Result<MediaInfo> probe(const char *url);
};