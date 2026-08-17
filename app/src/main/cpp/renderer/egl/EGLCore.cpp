#include "EGLCore.h"

#include <android/log.h>

#define TAG "EGLCore"

#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, TAG, __VA_ARGS__)
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, TAG, __VA_ARGS__)

EGLCore::EGLCore() = default;

EGLCore::~EGLCore() {
    release();
}

bool EGLCore::init(ANativeWindow *window) {
    if (window == nullptr) {
        LOGE("ANativeWindow is null.");
        return false;
    }
    nativeWindow = window;

    // Display
    display = eglGetDisplay(EGL_DEFAULT_DISPLAY);
    if (display == EGL_NO_DISPLAY) {
        LOGE("eglGetDisplay failed.");
        return false;
    }

    if (!eglInitialize(display, nullptr, nullptr)) {
        LOGE("eglInitialize failed.");
        release();
        return false;
    }

    // Config
    const EGLint configAttribs[] = {
            EGL_RENDERABLE_TYPE, EGL_OPENGL_ES2_BIT,
            EGL_SURFACE_TYPE, EGL_WINDOW_BIT,
            EGL_RED_SIZE, 8,
            EGL_GREEN_SIZE, 8,
            EGL_BLUE_SIZE, 8,
            EGL_ALPHA_SIZE, 8,
            EGL_NONE
    };

    EGLint numConfigs = 0;

    if (!eglChooseConfig(
            display,
            configAttribs,
            &config,
            1,
            &numConfigs
    )) {
        LOGE("eglChooseConfig failed.");

        release();

        return false;
    }

    // Context
    const EGLint contextAttribs[] = {
            EGL_CONTEXT_CLIENT_VERSION, 2,
            EGL_NONE
    };
    context = eglCreateContext(
            display,
            config,
            EGL_NO_CONTEXT,
            contextAttribs
    );
    if (context == EGL_NO_CONTEXT) {
        LOGE("eglCreateContext failed.");

        release();

        return false;
    }

    // Surface
    surface = eglCreateWindowSurface(
            display,
            config,
            nativeWindow,
            nullptr
    );

    if (surface == EGL_NO_SURFACE) {
        LOGE("eglCreateWindowSurface failed.");

        release();

        return false;
    }

    // Make current
//    if (!eglMakeCurrent(
//            display,
//            surface,
//            surface,
//            context)) {
//        LOGE("eglMakeCurrent failed.");
//
//        release();
//
//        return false;
//    }
    LOGI("EGL initialized.");
    return true;
}

bool EGLCore::makeCurrent() {
    if (!isValid()) {
        LOGE("EGL invalid");
        return false;
    }
    EGLBoolean result = eglMakeCurrent(
            display,
            surface,
            surface,
            context
    );

    if (!result) {
        LOGE(
                "eglMakeCurrent failed: 0x%x",
                eglGetError()
        );
        return false;
    }

    return true;
}

void EGLCore::swapBuffers() {
    if (!isValid()) {
        return;
    }
    eglSwapBuffers(
            display,
            surface
    );
}

int EGLCore::width() const {
    return ANativeWindow_getWidth(nativeWindow);
}

int EGLCore::height() const {
    return ANativeWindow_getHeight(nativeWindow);
}

void EGLCore::release() {
    if (display != EGL_NO_DISPLAY) {
        eglMakeCurrent(
                display,
                EGL_NO_SURFACE,
                EGL_NO_SURFACE,
                EGL_NO_CONTEXT
        );

        if (context != EGL_NO_CONTEXT) {
            eglDestroyContext(
                    display,
                    context
            );
            context = EGL_NO_CONTEXT;
        }

        if (surface != EGL_NO_SURFACE) {
            eglDestroySurface(
                    display,
                    surface
            );
            surface = EGL_NO_SURFACE;
        }

        eglTerminate(display);

        display = EGL_NO_DISPLAY;
    }

    nativeWindow = nullptr;
}

bool EGLCore::isValid() const {
    return display != EGL_NO_DISPLAY &&
            surface != EGL_NO_SURFACE &&
            context != EGL_NO_CONTEXT;
}

bool EGLCore::checkEglError(const char *op) {
    return false;
}
