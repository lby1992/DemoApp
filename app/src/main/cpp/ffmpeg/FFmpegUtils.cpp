#include "FFmpegUtils.h"

//#include <string>

extern "C" {
#include <libavutil/error.h>
#include <libavformat/avformat.h>
}

std::string FFmpegUtils::formatError(int code) {
    char buffer[AV_ERROR_MAX_STRING_SIZE] = {0};

    av_strerror(code, buffer, sizeof(buffer));
    return std::string(buffer);
}

StreamType FFmpegUtils::convertStreamType(int ffmpegType) {
    switch (ffmpegType) {
        case AVMEDIA_TYPE_VIDEO:
            return StreamType::Video;
        case AVMEDIA_TYPE_AUDIO:
            return StreamType::Audio;
        case AVMEDIA_TYPE_DATA:
            return StreamType::Data;
        case AVMEDIA_TYPE_SUBTITLE:
            return StreamType::Subtitle;
        case AVMEDIA_TYPE_ATTACHMENT:
            return StreamType::Attachment;
        case AVMEDIA_TYPE_NB:
            return StreamType::NB;
        case AVMEDIA_TYPE_UNKNOWN:
        default:
            return StreamType::Unknown;
    }
}
