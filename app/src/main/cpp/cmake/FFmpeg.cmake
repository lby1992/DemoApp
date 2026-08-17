# FFmpeg.cmake

set(
    FFMPEG_ROOT
    ${CMAKE_CURRENT_LIST_DIR}/../external/ffmpeg
)

set(
    FFMPEG_LIB_ROOT
    ${CMAKE_CURRENT_LIST_DIR}/../../jniLibs/${ANDROID_ABI}
)

set(
    FFMPEG_INCLUDE_DIR
    ${FFMPEG_ROOT}/include
)

# -------------------------------------
# avcodec
# -------------------------------------
add_library(
    avcodec
    SHARED
    IMPORTED
)

set_target_properties(
    avcodec
    PROPERTIES
    IMPORTED_LOCATION
    ${FFMPEG_LIB_ROOT}/libavcodec_baseus.so
)

# -------------------------------------
# avformat
# -------------------------------------
add_library(
    avformat
    SHARED
    IMPORTED
)

set_target_properties(
    avformat
    PROPERTIES
    IMPORTED_LOCATION
    ${FFMPEG_LIB_ROOT}/libavformat_baseus.so
)

# -------------------------------------
# avutil
# -------------------------------------
add_library(
    avutil
    SHARED
    IMPORTED
)

set_target_properties(
    avutil
    PROPERTIES
    IMPORTED_LOCATION
    ${FFMPEG_LIB_ROOT}/libavutil_baseus.so
)