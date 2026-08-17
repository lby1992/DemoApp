#include "NativePlayer.h"

#include <android/log.h>

extern "C" {
#include <libavutil/pixdesc.h>
}

#define TAG "NativePlayer"

#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, TAG, __VA_ARGS__)
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, TAG, __VA_ARGS__)

NativePlayer::NativePlayer() {

}

NativePlayer::~NativePlayer() {
    stop();
}

bool NativePlayer::open(const char *url) {
    auto result = decoder.open(url);

    return result.success;
}

bool NativePlayer::setSurface(ANativeWindow *window) {
    LOGI(
            "NativePlayer this=%p",
            this
    );
    if (window == nullptr) {
        return false;
    }

    std::lock_guard<std::mutex> lock(surfaceMutex);

    // Release old surface automatically
    nativeWindow.reset();

    // Take ownership
    nativeWindow.reset(window);

    return renderer.setSurface(nativeWindow.get());

//    if (!renderer.setSurface(nativeWindow.get())) {
//        return false;
//    }
//
//    renderer.render(nullptr);
//
//    return true;
}

void NativePlayer::play() {
    if (running) {
        return;
    }

    clock.start();

    running = true;

    decodeThread = std::thread(
            &NativePlayer::decodeLoop,
            this
    );
    renderThread = std::thread(
            &NativePlayer::renderLoop,
            this
    );
}

void NativePlayer::stop() {
    if (!running) {
        return;
    }

    running = false;

    frameQueue.abort();

    if (decodeThread.joinable()) {
        decodeThread.join();
    }

    if (renderThread.joinable()) {
        renderThread.join();
    }

    frameQueue.clear();
}

void NativePlayer::release() {
    // Stop playback first
    stop();
//
//    // Release renderer
//    renderer.release();

    // Release decoder
    decoder.close();

    // Clear frame queue
    frameQueue.clear();

    // Release surface
    {
        std::lock_guard<std::mutex> lock(surfaceMutex);
        nativeWindow.reset();
    }
}

void NativePlayer::decodeLoop() {
    while (running) {
        AVFrame *frame = av_frame_alloc();

        if (!decoder.decodeNextFrame(frame)) {
            av_frame_free(&frame);
            break;
        }

//        LOGI(
//                "decode frame pts=%ld",
//                frame->pts
//        );

        if (!frameQueue.push(frame)) {
            av_frame_free(&frame);

            break;
        }
    }
}

void NativePlayer::renderLoop() {
    LOGI("Render thread started.");

    while (running) {
        AVFrame *frame = frameQueue.pop();

        if (frame == nullptr) {
            continue;
        }

//        LOGD(
//                "format=%s width=%d height=%d",
//                av_get_pix_fmt_name((AVPixelFormat) frame->format),
//                frame->width,
//                frame->height
//        );

        waitForFrame(frame);

        {
            std::lock_guard<std::mutex> lock(surfaceMutex);

            if (nativeWindow.get() != nullptr) {
//                LOGI(
//                        "render frame pts=%ld",
//                        frame->pts
//                );
                renderer.render(frame);
            }
        }

        av_frame_free(&frame);
    }

    LOGI("Render thread releasing EGL.");

    renderer.release();

    LOGI("Render thread exited.");
}

void NativePlayer::waitForFrame(AVFrame *frame) {
    if (frame->pts == AV_NOPTS_VALUE) {
        return;
    }

    AVRational tb = decoder.timeBase();

    double pts = frame->pts * av_q2d(tb);

    double now = clock.getTime();

    double delay = now - pts;

    if (delay > 0) {
        std::this_thread::sleep_for(
                std::chrono::duration<double>(delay)
        );
    }
}
