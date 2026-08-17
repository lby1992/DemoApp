#include "HardwareDecoder.h"

extern "C" {
#include <libavutil/error.h>
}

#include "ffmpeg//FFmpegUtils.h"

HardwareDecoder::HardwareDecoder() = default;

HardwareDecoder::~HardwareDecoder() {
    close();
}

void HardwareDecoder::setSurface(ANativeWindow *window) {
    nativeWindow = window;
}

Result<bool> HardwareDecoder::open(const char *url) {

    Result<bool> result;

    int ret = avformat_open_input(
            &formatContext,
            url,
            nullptr,
            nullptr);

    if (ret < 0) {
        result.success = false;
        result.errorCode = ret;
        result.errorMessage = FFmpegUtils::formatError(ret);
        return result;
    }

    ret = avformat_find_stream_info(
            formatContext,
            nullptr);

    if (ret < 0) {
        close();

        result.success = false;
        result.errorCode = ret;
        result.errorMessage = FFmpegUtils::formatError(ret);

        return result;
    }

    for (unsigned int i = 0; i < formatContext->nb_streams; i++) {

        AVStream *stream = formatContext->streams[i];

        if (stream->codecpar->codec_type == AVMEDIA_TYPE_VIDEO) {

            videoStream = stream;

            videoStreamIndex = i;

            mTimeBase = stream->time_base;

            break;
        }
    }

    if (videoStream == nullptr) {

        close();

        result.success = false;
        result.errorMessage = "No video stream.";

        return result;
    }

    packet = av_packet_alloc();

    if (!packet) {

        close();

        result.success = false;
        result.errorMessage = "Cannot alloc packet.";

        return result;
    }

    if (!createMediaCodec()) {

        close();

        result.success = false;
        result.errorMessage = "Create MediaCodec failed.";

        return result;
    }

    result.success = true;
    result.data = true;

    return result;
}

void HardwareDecoder::releaseMediaCodec() {

    if (mediaCodec) {

        if (started) {
            AMediaCodec_stop(mediaCodec);
            started = false;
        }

        AMediaCodec_delete(mediaCodec);

        mediaCodec = nullptr;
    }

    if (mediaFormat) {
        AMediaFormat_delete(mediaFormat);
        mediaFormat = nullptr;
    }
}

void HardwareDecoder::close() {

    releaseMediaCodec();

    if (packet) {
        av_packet_free(&packet);
    }

    if (formatContext) {
        avformat_close_input(&formatContext);
    }

    videoStream = nullptr;
    videoStreamIndex = -1;
}

bool HardwareDecoder::decodeNextFrame(AVFrame *frame) {
    return false;
}

AVRational HardwareDecoder::timeBase() const {
    return mTimeBase;
}

bool HardwareDecoder::createMediaCodec() {
    mimeType = getMimeType();

    if (!mimeType) {
        return false;
    }

    if (!createMediaFormat()) {
        return false;
    }

    if (!setCodecSpecificData()) {
        return false;
    }

    mediaCodec = AMediaCodec_createDecoderByType(mimeType);

    if (!mediaCodec) {
        return false;
    }

    media_status_t status = AMediaCodec_configure(
            mediaCodec,
            mediaFormat,
            nativeWindow,
            nullptr,
            0
    );
    return false;
}

const char *HardwareDecoder::getMimeType() const {
    if (!videoStream) {
        return nullptr;
    }

    switch (videoStream->codecpar->codec_id) {
        case AV_CODEC_ID_H264:
            return "video/avc";
        case AV_CODEC_ID_HEVC:
            return "video/hevc";
        default:
            return nullptr;
    }
}

bool HardwareDecoder::createMediaFormat() {
    mediaFormat = AMediaFormat_new();

    if (!mediaFormat) {
        return false;
    }

    AMediaFormat_setString(
            mediaFormat,
            AMEDIAFORMAT_KEY_MIME,
            mimeType
    );

    AMediaFormat_setInt32(
            mediaFormat,
            AMEDIAFORMAT_KEY_WIDTH,
            videoStream->codecpar->width
    );
    AMediaFormat_setInt32(
            mediaFormat,
            AMEDIAFORMAT_KEY_HEIGHT,
            videoStream->codecpar->height
    );

    return true;
}

bool HardwareDecoder::setCodecSpecificData() {
    auto codecpar = videoStream->codecpar;

    if (!codecpar->extradata || codecpar->extradata_size <= 0) {
        return false;
    }

    if (codecpar->codec_id == AV_CODEC_ID_H264) {
        AMediaFormat_setBuffer(
                mediaFormat,
                "csd-0",
                codecpar->extradata,
                codecpar->extradata_size
        );

        return true;
    }

    return false;
}
