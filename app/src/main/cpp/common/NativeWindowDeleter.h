#pragma once

#include <android/native_window.h>
#include <memory>

struct NativeWindowDeleter {
    void operator()(ANativeWindow *window) const {
        if (window != nullptr) {
            ANativeWindow_release(window);
        }
    }
};

using NativeWindowPtr = std::unique_ptr<ANativeWindow, NativeWindowDeleter>;