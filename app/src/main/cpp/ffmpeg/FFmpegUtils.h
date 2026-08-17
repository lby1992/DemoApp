#pragma once

#include <string>

#include "model/StreamType.h"

class FFmpegUtils {
public:
    static std::string formatError(
            int code
    );

    static StreamType convertStreamType(
            int ffmpegType
    );

};