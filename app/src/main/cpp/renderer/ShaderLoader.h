#pragma once

#include <string>

class ShaderLoader {
public:
    static std::string load(
            const char *path
    );
};