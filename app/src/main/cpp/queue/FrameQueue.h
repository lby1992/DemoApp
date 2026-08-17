#pragma once

#include <queue>
#include <mutex>
#include <condition_variable>

extern "C" {
#include <libavutil/frame.h>
}

class FrameQueue {
public:
    explicit FrameQueue(
            size_t maxSize = 8
            );

    ~FrameQueue();

    bool push(
            AVFrame* frame
            );

    AVFrame* pop();

    void clear();

    void abort();

private:
    std::queue<AVFrame*> queue;
    std::mutex mutex;
    std::condition_variable condition;
    size_t maxSize;
    bool abortRequest = false;
};