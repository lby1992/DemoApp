#include "FFmpegProbe.h"

extern "C" {
#include <libavformat/avformat.h>
#include <libavcodec/codec_id.h>
}

#include "FFmpegUtils.h"

Result<MediaInfo> FFmpegProbe::probe(const char *url) {
    Result<MediaInfo> result;

    AVFormatContext *fmt = nullptr;

    // Open input
    int ret = avformat_open_input(&fmt,
            url,
            nullptr,
            nullptr);
    if (ret < 0) {
        result.success = false;
        result.errorCode = ret;
        result.errorMessage = FFmpegUtils::formatError(ret);

        return result;
    }

    // Find stream info
    ret = avformat_find_stream_info(fmt, nullptr);
    if (ret < 0) {
        result.success = false;
        result.errorCode = ret;
        result.errorMessage = FFmpegUtils::formatError(ret);

        return result;
    }

    MediaInfo mediaInfo;
    //
    // Duration
    //
    if (fmt->duration > 0) {
        mediaInfo.durationMs = fmt->duration * 1000 / AV_TIME_BASE;
    }

    //
    // Streams
    //
    for (unsigned int i = 0; i < fmt->nb_streams; i++) {
        AVStream *stream = fmt->streams[i];

        auto *codecpar = stream->codecpar;
        StreamInfo info;
        AVRational frameRate;

        info.index = static_cast<int>(i);
        info.type = FFmpegUtils::convertStreamType(codecpar->codec_type);
        info.codecId = static_cast<int>(codecpar->codec_id);
        const char *codecName = avcodec_get_name(codecpar->codec_id);

        if (codecName != nullptr) {
            info.codec = codecName;
        }
        info.bitrate = codecpar->bit_rate;

        switch (stream->codecpar->codec_type) {
            case AVMEDIA_TYPE_UNKNOWN:
                break;
            case AVMEDIA_TYPE_VIDEO:
                info.width = codecpar->width;
                info.height = codecpar->height;
                frameRate = stream->avg_frame_rate;
                if (frameRate.den != 0) {
                    info.fps = av_q2d(frameRate);
                }
                break;
            case AVMEDIA_TYPE_AUDIO:
                info.sampleRate = codecpar->sample_rate;
                info.channels = codecpar->ch_layout.nb_channels;
                break;
            case AVMEDIA_TYPE_DATA:
                break;
            case AVMEDIA_TYPE_SUBTITLE:
                break;
            case AVMEDIA_TYPE_ATTACHMENT:
                break;
            case AVMEDIA_TYPE_NB:
                break;
        }

        mediaInfo.streams.push_back(info);
    }

    avformat_close_input(&fmt);
    result.success = true;
    result.data = mediaInfo;

    return result;
}
