cmake_minimum_required(VERSION 3.22)

project(demoapp)

add_library(
    native-lib
    SHARED
    native-lib.cpp
)

add_library(
    ffmpeg-lib
    SHARED
    ffmpeg-lib.cpp
)

target_include_directories(ffmpeg-lib PRIVATE
    ${CMAKE_SOURCE_DIR}/third_party/ffmpeg/include
)

target_include_directories(ffmpeg-lib PRIVATE
    ${CMAKE_SOURCE_DIR}/third_party/nlohmann
)

add_library(
    avcodec
    SHARED
    IMPORTED
)

set_target_properties(
    avcodec
    PROPERTIES
    IMPORTED_LOCATION
    ${CMAKE_SOURCE_DIR}/../jniLibs/${ANDROID_ABI}/libavcodec_baseus.so
)

add_library(
    avformat
    SHARED
    IMPORTED
)

set_target_properties(
    avformat
    PROPERTIES
    IMPORTED_LOCATION
    ${CMAKE_SOURCE_DIR}/../jniLibs/${ANDROID_ABI}/libavformat_baseus.so
)

add_library(
    avutil
    SHARED
    IMPORTED
)

set_target_properties(
    avutil
    PROPERTIES
    IMPORTED_LOCATION
    ${CMAKE_SOURCE_DIR}/../jniLibs/${ANDROID_ABI}/libavutil_baseus.so
)

find_library(
    log-lib
    log
)

target_link_libraries(
    native-lib
    ${log-lib}
    avcodec
    avformat
    avutil
)

target_link_libraries(
    ffmpeg-lib
    ${log-lib}
    avcodec
    avformat
    avutil
)