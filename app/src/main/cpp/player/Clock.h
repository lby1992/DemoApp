#pragma once

#include <chrono>
#include <mutex>

class Clock {

public:
    Clock();

    void start();

    void reset();

    double getTime();

private:
    std::chrono::steady_clock::time_point startTime;

    bool started = false;
};