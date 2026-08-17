#include "FFmpegDecoder.h"

extern "C" {
#include <libavutil/error.h>
}

#include "FFmpegUtils.h"

FFmpegDecoder::FFmpegDecoder() {

}

FFmpegDecoder::~FFmpegDecoder() {
    close();
}

Result<bool> FFmpegDecoder::open(const char *url) {
    Result<bool> result;

    // Open conatainer
    int ret = avformat_open_input(
            &formatContext,
            url,
            nullptr,
            nullptr
    );

    if (ret < 0) {
        result.success = false;
        result.errorCode = ret;
        result.errorMessage = FFmpegUtils::formatError(ret);

        return result;
    }

    // Read stream information
    ret = avformat_find_stream_info(
            formatContext,
            nullptr
    );
    if (ret < 0) {
        result.success = false;
        result.errorCode = ret;
        result.errorMessage = FFmpegUtils::formatError(ret);

        close();
        return result;
    }

    // Find video stream
    for (unsigned int i = 0; i < formatContext->nb_streams; i++) {
        AVStream *stream = formatContext->streams[i];

        auto codecpar = stream->codecpar;
        if (codecpar->codec_type == AVMEDIA_TYPE_VIDEO) {
            videoStream = stream;
            videoStreamIndex = static_cast<int>(i);
            break;
        }
    }

    if (videoStreamIndex < 0) {

        result.success = false;

        result.errorMessage =
                "No video stream found";

        close();

        return result;
    }

    // Find decoder
    codec = avcodec_find_decoder(videoStream->codecpar->codec_id);
    if (codec == nullptr) {
        result.success = false;

        result.errorMessage = "Decoder not found.";

        close();

        return result;
    }

    // Allocate codec context
    codecContext = avcodec_alloc_context3(codec);
    if (codecContext == nullptr) {
        result.success = false;

        result.errorMessage =
                "Cannot allocate codec context";

        close();

        return result;
    }

    // Copy codec parameters
    ret = avcodec_parameters_to_context(codecContext, videoStream->codecpar);
    if (ret < 0) {
        result.success = false;
        result.errorCode = ret;
        result.errorMessage =
                FFmpegUtils::formatError(ret);
        close();
        return result;
    }

    // Open decoder
    ret = avcodec_open2(codecContext, codec, nullptr);
    if (ret < 0) {
        result.success = false;
        result.errorCode = ret;
        result.errorMessage =
                FFmpegUtils::formatError(ret);

        close();
        return result;
    }

    result.success = true;
    result.data = true;

    return result;
}

bool FFmpegDecoder::decodeNextFrame(AVFrame *frame) {
    AVPacket* packet = av_packet_alloc();

    if (packet == nullptr) {
        return false;
    }

    while (av_read_frame(formatContext, packet) >= 0) {
        // Ignore audio packet
        if (packet->stream_index != videoStreamIndex) {
            av_packet_unref(packet);

            continue;
        }

        int ret = avcodec_send_packet(codecContext, packet);
        av_packet_unref(packet);

        if (ret < 0) {
            av_packet_free(&packet);
            return false;
        }

        while (true) {
            ret = avcodec_receive_frame(codecContext, frame);

            if (ret == AVERROR(EAGAIN)) {
                break;
            }

            if (ret == AVERROR_EOF) {
                av_packet_free(&packet);
                return false;
            }

            if (ret < 0) {
                av_packet_free(&packet);
                return false;
            }

            // Got frame
            av_packet_free(&packet);
            return true;
        }
    }

    av_packet_free(&packet);

    return false;
}

AVRational FFmpegDecoder::timeBase() const
{
    return videoStream->time_base;
}

void FFmpegDecoder::close() {
    if (codecContext) {
        avcodec_free_context(&codecContext);
    }

    if (formatContext) {
        avformat_close_input(&formatContext);
    }

    reset();
}

bool FFmpegDecoder::sendPacket(AVPacket *packet, AVFrame *frame) {
    return false;
}

void FFmpegDecoder::reset() {
    codec = nullptr;
    videoStream = nullptr;
    videoStreamIndex = -1;
}