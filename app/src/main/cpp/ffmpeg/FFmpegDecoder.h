#pragma once

#include "model/Result.h"

extern "C" {
#include <libavformat/avformat.h>
#include <libavcodec/avcodec.h>
#include <libavcodec/packet.h>
#include <libavutil/frame.h>
}

class FFmpegDecoder {
public:
    FFmpegDecoder();

    ~FFmpegDecoder();

    Result<bool> open(
            const char *url
    );

    bool decodeNextFrame(
            AVFrame *frame
    );

    AVRational timeBase() const;

    void close();

private:
    AVFormatContext *formatContext = nullptr;

    AVCodecContext *codecContext = nullptr;

    const AVCodec *codec = nullptr;

    AVStream *videoStream = nullptr;

    int videoStreamIndex = -1;

private:
    bool sendPacket(
            AVPacket *packet,
            AVFrame *frame
    );

    void reset();
};