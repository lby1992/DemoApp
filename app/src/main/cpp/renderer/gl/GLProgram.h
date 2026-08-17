#pragma once

#include <string>
#include <GLES2/gl2.h>

class GLProgram {

public:
    GLProgram();

    ~GLProgram();

    bool load(
            const std::string &vertexSource,
            const std::string &fragmentSource
    );

    void use() const;

    GLuint id() const;

    void release();

    GLint getAttributeLocation(
            const char *name
    ) const;

    GLint getUniformLocation(
            const char *name
    ) const;

private:
    GLuint program = 0;
private:
    GLuint compileShader(
            GLenum type,
            const std::string &source
    );
};