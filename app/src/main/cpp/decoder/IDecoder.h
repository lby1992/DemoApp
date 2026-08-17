#pragma once

extern "C" {
#include <libavcodec/avcodec.h>
}

#include "model/Result.h"

class IDecoder {

public:
    virtual ~IDecoder() = default;

    virtual Result<bool> open(const char *url) = 0;

    virtual bool decodeNextFrame(AVFrame *frame) = 0;

    virtual AVRational timeBase() const = 0;

    virtual void close() = 0;

};