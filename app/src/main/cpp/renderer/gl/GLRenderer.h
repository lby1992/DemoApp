#pragma once

#include <android/native_window.h>
#include <GLES2/gl2.h>
#include <libavutil/frame.h>

#include "renderer/egl/EGLCore.h"
#include "GLProgram.h"
#include "../Quad.h"


class GLRenderer {

public:
    GLRenderer();

    ~GLRenderer();

    bool setSurface(ANativeWindow *window);

    void render(AVFrame *frame);

    void release();

private:
    EGLCore eglCore;

    GLProgram program;

    bool initialized = false;

    GLuint yTexture = 0;
    GLuint uTexture = 0;
    GLuint vTexture = 0;

    GLint positionLocation = -1;
    GLint texCoordLocation = -1;

    GLint texYLocation = -1;
    GLint texULocation = -1;
    GLint texVLocation = -1;

    int videoWidth = 0;
    int videoHeight = 0;

    Quad quad;

private:
    bool initShader();

    bool initTexture();

    static void setupTexture(GLuint texture);

    void updateTextures(
            AVFrame *frame
    );

    void draw();
};