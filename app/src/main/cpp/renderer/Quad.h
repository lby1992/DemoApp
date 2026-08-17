#pragma once

#include <GLES2/gl2.h>

class Quad {
public:
    Quad() = default;

    void setVertices(
            const GLfloat *vertices
    );
//
//    void setScaleType(
//            ScaleTye type
//    );

    void setVideoSize(
            int width,
            int height
    );

    void setViewSize(
            int width,
            int height
    );

    void draw(
            GLint positionLocation,
            GLint texCoordLocation
    );

private:
    // Simple vertices. TODO covert to VBO VAO
    static const GLfloat VERTICES[16];
};