#include "GLRenderer.h"
#include "libavutil/frame.h"

#include <android/log.h>
#include <string>

#include "../ShaderLoader.h"

#define TAG "GLRenderer"

#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, TAG, __VA_ARGS__)


GLRenderer::GLRenderer() = default;

GLRenderer::~GLRenderer() {
    release();
}

bool GLRenderer::setSurface(ANativeWindow *window) {
    if (window == nullptr) {
        LOGE("window is null");
        return false;
    }

    LOGI("SET SUrface");

    return eglCore.init(window);
}

void GLRenderer::render(AVFrame *frame) {
    if (!eglCore.makeCurrent()) {
        LOGE("makeCurrent failed");
        return;
    }

    if (!initialized) {
        if (!initShader()) {
            LOGE("initShader failed.");
            return;
        }

        if (!initTexture()) {
            LOGE("initTexture failed.");
            return;
        }

        glUniform1i(
                texYLocation,
                0
        );

        glUniform1i(
                texULocation,
                1
        );

        glUniform1i(
                texVLocation,
                2
        );

        initialized = true;
    }

    int width = eglCore.width();
    int height = eglCore.height();
    if (width <= 0 || height <= 0) {
        LOGE(
                "Invalid window size %d x %d",
                width,
                height
        );

        return;
    }

//    LOGI(
//            "render pts=%ld",
//            frame->pts
//    );

    glViewport(
            0,
            0,
            width,
            height
    );

    glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

    glClear(GL_COLOR_BUFFER_BIT);

    program.use();

    updateTextures(frame);

    draw();

    eglCore.swapBuffers();
}

void GLRenderer::release() {
    eglCore.release();

    glDeleteTextures(
            1,
            &yTexture
    );

    glDeleteTextures(
            1,
            &uTexture
    );

    glDeleteTextures(
            1,
            &vTexture
    );

    program.release();
}

bool GLRenderer::initShader() {
    std::string vs =
            ShaderLoader::load(
                    "shaders/yuv420.vert"
            );

    std::string fs =
            ShaderLoader::load(
                    "shaders/yuv420.frag"
            );

    if (!program.load(vs, fs)) {
        return false;
    }

    positionLocation = program.getAttributeLocation("position");

    texCoordLocation = program.getAttributeLocation("texCoord");

    texYLocation = program.getUniformLocation("texY");
    texULocation = program.getUniformLocation("texU");
    texVLocation = program.getUniformLocation("texV");

    LOGI("Shader initialized.");

    return true;
}

bool GLRenderer::initTexture() {
    glGenTextures(1, &yTexture);
    glGenTextures(1, &uTexture);
    glGenTextures(1, &vTexture);

    setupTexture(yTexture);
    setupTexture(uTexture);
    setupTexture(vTexture);

    LOGI("Textures created.");
    return true;
}

void GLRenderer::updateTextures(AVFrame *frame) {
    if (frame == nullptr) {
        return;
    }

    glPixelStorei(
            GL_UNPACK_ALIGNMENT,
            1
    );

    if (videoWidth != frame->width || videoHeight != frame->height) {
        // Y

        glBindTexture(
                GL_TEXTURE_2D,
                yTexture
        );

        glTexImage2D(
                GL_TEXTURE_2D,
                0,
                GL_LUMINANCE,
                frame->width,
                frame->height,
                0,
                GL_LUMINANCE,
                GL_UNSIGNED_BYTE,
                nullptr
        );


        // U

        glBindTexture(
                GL_TEXTURE_2D,
                uTexture
        );

        glTexImage2D(
                GL_TEXTURE_2D,
                0,
                GL_LUMINANCE,
                frame->width / 2,
                frame->height / 2,
                0,
                GL_LUMINANCE,
                GL_UNSIGNED_BYTE,
                nullptr
        );


        // V

        glBindTexture(
                GL_TEXTURE_2D,
                vTexture
        );

        glTexImage2D(
                GL_TEXTURE_2D,
                0,
                GL_LUMINANCE,
                frame->width / 2,
                frame->height / 2,
                0,
                GL_LUMINANCE,
                GL_UNSIGNED_BYTE,
                nullptr
        );


        videoWidth = frame->width;
        videoHeight = frame->height;
    }

    // Y
    glActiveTexture(GL_TEXTURE0);

    glBindTexture(
            GL_TEXTURE_2D,
            yTexture
    );

    glTexSubImage2D(
            GL_TEXTURE_2D,
            0,
            0,
            0,
            frame->width,
            frame->height,
            GL_LUMINANCE,
            GL_UNSIGNED_BYTE,
            frame->data[0]
    );

    // U
    glActiveTexture(GL_TEXTURE1);

    glBindTexture(
            GL_TEXTURE_2D,
            uTexture
    );

    glTexSubImage2D(
            GL_TEXTURE_2D,
            0,
            0,
            0,
            frame->width / 2,
            frame->height / 2,
            GL_LUMINANCE,
            GL_UNSIGNED_BYTE,
            frame->data[1]
    );

    // V

    glActiveTexture(GL_TEXTURE2);

    glBindTexture(
            GL_TEXTURE_2D,
            vTexture
    );

    glTexSubImage2D(
            GL_TEXTURE_2D,
            0,
            0,
            0,
            frame->width / 2,
            frame->height / 2,
            GL_LUMINANCE,
            GL_UNSIGNED_BYTE,
            frame->data[2]
    );
//
//    LOGI(
//            "Y linesize=%d width=%d",
//            frame->linesize[0],
//            frame->width
//    );

    // bind sampler
    glUniform1i(
            texYLocation,
            0
    );

    glUniform1i(
            texULocation,
            1
    );

    glUniform1i(
            texVLocation,
            2
    );
}

void GLRenderer::draw() {
    quad.draw(positionLocation, texCoordLocation);
}

void GLRenderer::setupTexture(GLuint texture) {
    glBindTexture(
            GL_TEXTURE_2D,
            texture
    );

    glTexParameteri(
            GL_TEXTURE_2D,
            GL_TEXTURE_MIN_FILTER,
            GL_LINEAR
    );

    glTexParameteri(
            GL_TEXTURE_2D,
            GL_TEXTURE_MAG_FILTER,
            GL_LINEAR
    );

    glTexParameteri(
            GL_TEXTURE_2D,
            GL_TEXTURE_WRAP_S,
            GL_CLAMP_TO_EDGE
    );

    glTexParameteri(
            GL_TEXTURE_2D,
            GL_TEXTURE_WRAP_T,
            GL_CLAMP_TO_EDGE
    );

    glBindTexture(
            GL_TEXTURE_2D,
            0
    );
}
