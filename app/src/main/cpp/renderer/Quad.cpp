#include "Quad.h"

const GLfloat Quad::VERTICES[16] = {
        // x      y       u      v

        -1.0f, 1.0f, 0.0f, 0.0f,

        -1.0f, -1.0f, 0.0f, 1.0f,

        1.0f, 1.0f, 1.0f, 0.0f,

        1.0f, -1.0f, 1.0f, 1.0f
};

void Quad::draw(GLint positionLocation, GLint texCoordLocation) {
    glVertexAttribPointer(
            positionLocation,
            2,
            GL_FLOAT,
            GL_FALSE,
            4 * sizeof(GLfloat),
            VERTICES
    );

    glEnableVertexAttribArray(positionLocation);

    glVertexAttribPointer(
            texCoordLocation,
            2,
            GL_FLOAT,
            GL_FALSE,
            4 * sizeof(GLfloat),
            VERTICES + 2
    );

    glEnableVertexAttribArray(texCoordLocation);

    glDrawArrays(
            GL_TRIANGLE_STRIP,
            0,
            4
    );

    glDisableVertexAttribArray(positionLocation);
    glDisableVertexAttribArray(texCoordLocation);
}

void Quad::setVertices(const GLfloat *vertices) {

}
//
//void Quad::setScaleType(ScaleTye type) {
//
//}

void Quad::setVideoSize(int width, int height) {

}

void Quad::setViewSize(int width, int height) {

}
