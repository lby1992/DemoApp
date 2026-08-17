#pragma once

#include "Clock.h"
#include "ffmpeg/FFmpegDecoder.h"
#include "queue/FrameQueue.h"
#include "renderer/gl/GLRenderer.h"
#include "common/NativeWindowDeleter.h"

#include <android/native_window.h>

#include <thread>
#include <atomic>

class NativePlayer {
public:
    NativePlayer();

    ~NativePlayer();

    bool open(
            const char *url
    );

    bool setSurface(ANativeWindow* window);

    void play();

    void stop();

    void release();

private:
    FFmpegDecoder decoder;
    FrameQueue frameQueue;
    GLRenderer renderer;
    NativeWindowPtr nativeWindow;
    std::thread decodeThread;
    std::thread renderThread;
    std::atomic<bool> running = false;
    std::atomic<bool> renderStarted = false;
    // Protect Surface replacement
    std::mutex surfaceMutex;

    Clock clock;
private:
    void decodeLoop();
    void renderLoop();
    void waitForFrame(AVFrame* frame);
};