#include "GLProgram.h"

#include <android/log.h>

#define TAG "GLProgram"

#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, TAG, __VA_ARGS__)
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, TAG, __VA_ARGS__)

GLProgram::GLProgram() = default;

GLProgram::~GLProgram() {
    release();
}

bool GLProgram::load(
        const std::string &vertexSource,
        const std::string &fragmentSource
) {
    release();

    GLuint vertexShader = compileShader(
            GL_VERTEX_SHADER,
            vertexSource
    );

    if (vertexShader == 0) {
        return false;
    }

    GLuint fragmentShader = compileShader(
            GL_FRAGMENT_SHADER,
            fragmentSource
    );

    if (fragmentShader == 0) {
        glDeleteShader(vertexShader);
        return false;
    }

    program = glCreateProgram();

    glAttachShader(
            program,
            vertexShader
    );

    glAttachShader(
            program,
            fragmentShader
    );

    glLinkProgram(program);

    glDeleteShader(vertexShader);
    glDeleteShader(fragmentShader);

    GLint success = GL_FALSE;

    glGetProgramiv(
            program,
            GL_LINK_STATUS,
            &success
    );

    if (!success) {
        char log[1024];

        glGetProgramInfoLog(
                program,
                sizeof(log),
                nullptr,
                log
        );

        LOGE("Link program failed:\n%s", log);

        release();

        return false;
    }

    return true;
}

void GLProgram::use() const {
    glUseProgram(program);
}

GLuint GLProgram::id() const {
    return program;
}

void GLProgram::release() {
    if (program) {
        glDeleteProgram(program);

        program = 0;
    }
}

GLint GLProgram::getAttributeLocation(const char *name) const {
    return glGetAttribLocation(
            program,
            name
    );
}

GLint GLProgram::getUniformLocation(const char *name) const {
    return glGetUniformLocation(
            program,
            name
    );
}

GLuint GLProgram::compileShader(GLenum type, const std::string &source) {
    GLuint shader = glCreateShader(type);

    const char *text = source.c_str();

    glShaderSource(
            shader,
            1,
            &text,
            nullptr
    );

    glCompileShader(shader);

    GLint success = GL_FALSE;

    glGetShaderiv(
            shader,
            GL_COMPILE_STATUS,
            &success
    );

    if (!success) {
        char log[1024];

        glGetShaderInfoLog(
                shader,
                sizeof(log),
                nullptr,
                log
        );

        LOGE("Compile shader failed:\n%s", log);

        glDeleteShader(shader);

        return 0;
    }

    return shader;
}
