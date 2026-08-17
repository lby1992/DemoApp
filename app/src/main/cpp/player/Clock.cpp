#include "Clock.h"

Clock::Clock() {
    reset();
}

void Clock::start() {
    startTime = std::chrono::steady_clock::now();

    started = true;
}

void Clock::reset() {
    started = false;
}

// Returns in second
double Clock::getTime() {
    if (!started) {
        return 0;
    }

    auto now = std::chrono::steady_clock::now();

    auto diff = std::chrono::duration<double>(now - startTime);

    return diff.count();
}
