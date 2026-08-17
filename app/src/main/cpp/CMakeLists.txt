cmake_minimum_required(VERSION 3.22)

project(demoapp)

set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

include(cmake/FFmpeg.cmake)

# -------------------------------------
# Collect sources
# -------------------------------------
file(GLOB_RECURSE
    CPP_SOURCES
    CONFIGURE_DEPENDS
    "*.cpp"
)
message("CPP SOURCES:")
foreach(src ${CPP_SOURCES})
    message(${src})
endforeach()

# -------------------------------------
# Native library
# -------------------------------------
add_library(
    NativeLib
    SHARED
    ${CPP_SOURCES}
)

# -------------------------------------
# Include directories
# -------------------------------------
target_include_directories(
    NativeLib
    PRIVATE
    ${CMAKE_SOURCE_DIR}
    ${FFMPEG_INCLUDE_DIR}
)

# json
# target_include_directories(ffmpeg-lib PRIVATE
#    ${CMAKE_SOURCE_DIR}/external/nlohmann
# )


# -------------------------------------
# Android libraries
# -------------------------------------
find_library(
    log-lib
    log
)

find_library(
        GLES-lib
        GLESv2
)

find_library(
        android-lib
        android
)


find_library(
        EGL-lib
        EGL
)

# -------------------------------------
# Link
# -------------------------------------
target_link_libraries(
    NativeLib

    ${log-lib}

    ${android-lib}

    ${GLES-lib}

    ${EGL-lib}

    avcodec
    avformat
    avutil
)