#include "FrameQueue.h"

#include <android/log.h>

#define TAG "FrameQueue"

#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, TAG, __VA_ARGS__)

FrameQueue::FrameQueue(size_t maxSize) : maxSize(maxSize) {
}

FrameQueue::~FrameQueue() {
    clear();
}

bool FrameQueue::push(AVFrame *frame) {
    if (frame == nullptr) {
        return false;
    }

    std::unique_lock<std::mutex> lock(mutex);

//    condition.wait(
//            lock,
//            [this] {
//                return abortRequest ||
//                        queue.size() < maxSize;
//            }
//    );

    if (abortRequest) {
        av_frame_free(&frame);
        return false;
    }

    LOGI(
            "queue size=%zu",
            queue.size()
    );

    // Drop the oldest one
    if (queue.size() >= maxSize) {
        AVFrame* oldestFrame = queue.front();

        queue.pop();

        av_frame_free(&oldestFrame);
    }

    queue.push(frame);

    condition.notify_one();
//    condition.notify_all();

    return true;
}

AVFrame *FrameQueue::pop() {
    std::unique_lock<std::mutex> lock(mutex);

    condition.wait(
            lock,
            [this] {
                return abortRequest || !queue.empty();
            }
    );

    if (abortRequest) {
        return nullptr;
    }

    AVFrame* frame = queue.front();

    queue.pop();

    condition.notify_all();

    return frame;
}

void FrameQueue::clear() {
    std::lock_guard<std::mutex> lock(mutex);

    while (!queue.empty()) {
        AVFrame* frame = queue.front();

        queue.pop();

        av_frame_free(&frame);
    }
}

void FrameQueue::abort() {
    std::lock_guard<std::mutex> lock(mutex);

    abortRequest = true;

    condition.notify_all();
}
