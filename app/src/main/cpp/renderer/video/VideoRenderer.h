#pragma once

#include <stdint.h>

extern "C" {
#include "external/ffmpeg/include/libavutil/frame.h"
}

class VideoRenderer {

public:
    virtual  ~VideoRenderer() = default;

    virtual bool init(int width, int height) = 0;

    virtual void render(AVFrame* frame) = 0;

    virtual void release() = 0;
};