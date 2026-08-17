#pragma once

#include <android/native_window.h>

#include <EGL/egl.h>

class EGLCore {

public:
    EGLCore();

    ~EGLCore();

    bool init(
            ANativeWindow *window
    );

    bool makeCurrent();

    void swapBuffers();

    int width() const;

    int height() const;

    void release();

    bool isValid() const;

private:
    EGLDisplay display = EGL_NO_DISPLAY;

    EGLSurface surface = EGL_NO_SURFACE;

    EGLContext context = EGL_NO_CONTEXT;

    EGLConfig config = nullptr;

    ANativeWindow *nativeWindow = nullptr;

private:
    bool checkEglError(const char* op);
};