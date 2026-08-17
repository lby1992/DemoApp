#pragma once

extern "C" {
#include <libavformat/avformat.h>
#include <libavcodec/avcodec.h>
}

#include <media/NdkMediaCodec.h>
#include <media/NdkMediaFormat.h>

#include "model/Result.h"
#include "IDecoder.h"

class HardwareDecoder : public IDecoder {
public:
    HardwareDecoder();

    ~HardwareDecoder() override;

    Result<bool> open(const char *url) override;

    bool decodeNextFrame(AVFrame * frame) override;

    AVRational timeBase() const override;

    void close() override;

    void setSurface(ANativeWindow *window);

private:
    bool createMediaCodec();

    void releaseMediaCodec();

    const char* getMimeType() const;

    bool createMediaFormat();

    bool setCodecSpecificData();

    bool startMediaCoded();

    bool starMediaCodec();
private:
    const char* mimeType{};
    // --------------------- FFmpeg ---------------------
    AVFormatContext *formatContext = nullptr;

    AVStream *videoStream = nullptr;

    int videoStreamIndex = -1;

    AVPacket *packet = nullptr;

    AVRational mTimeBase{};

    // ---------- Android ----------

    ANativeWindow *nativeWindow = nullptr;

    AMediaCodec *mediaCodec = nullptr;

    AMediaFormat *mediaFormat = nullptr;

    bool started = false;
};