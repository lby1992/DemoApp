#pragma once

#include <string>

template<typename T>
struct Result {
    bool success = false;

    int errorCode = 0;

    std::string errorMessage;

    T data;
};