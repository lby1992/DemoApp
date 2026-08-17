precision mediump float;

varying vec2 vTexCoord;

uniform sampler2D texY;
uniform sampler2D texU;
uniform sampler2D texV;


void main()
{
    float y =
        texture2D(texY, vTexCoord).r;

    float u =
        texture2D(texU, vTexCoord).r - 0.5;

    float v =
        texture2D(texV, vTexCoord).r - 0.5;


    // BT.709

    float r =
        y + 1.5748 * v;

    float g =
        y - 0.1873 * u - 0.4681 * v;

    float b =
        y + 1.8556 * u;


    gl_FragColor =
        vec4(
            r,
            g,
            b,
            1.0
        );
}