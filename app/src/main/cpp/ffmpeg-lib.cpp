//
// Created by libiy on 5/31/2026.
//
#include <jni.h>
#include <string>
#include <android/log.h>

extern "C"
{
#include <libavformat/avformat.h>
#include <libavcodec/avcodec.h>
#include <libavutil/log.h>
}

static void ffmpegLogCallback(
        void *ptr,
        int level,
        const char *fmt,
        va_list vl) {

    char buffer[1024];

    vsnprintf(
            buffer,
            sizeof(buffer),
            fmt,
            vl);

    __android_log_print(
            ANDROID_LOG_DEBUG,
            "FFMPEG",
            "%s",
            buffer);
}

JNIEXPORT jint JNICALL
JNI_OnLoad(
        JavaVM *vm,
        void *reserved) {

    av_log_set_level(AV_LOG_TRACE);

    av_log_set_callback(
            ffmpegLogCallback);

    __android_log_print(
            ANDROID_LOG_INFO,
            "FFMPEG",
            "FFmpeg log initialized");

    return JNI_VERSION_1_6;
}

extern "C"
JNIEXPORT jint JNICALL
Java_dev_dl_demoapp_core_jni_NativeLib_getFfmpegVersion(JNIEnv *env, jobject thiz) {
    return avformat_version();
}

extern "C"
JNIEXPORT jstring JNICALL
Java_dev_dl_demoapp_core_jni_NativeLib_probeRtsp(JNIEnv *env, jobject thiz, jstring url_) {
    const char *url = env->GetStringUTFChars(url_, nullptr);
    AVFormatContext *fmt = nullptr;

//    int ret = avformat_open_input(&fmt, url, nullptr, nullptr);
    AVDictionary *opts = nullptr;

    av_dict_set(&opts, "rtsp_transport", "tcp", 0);
    av_dict_set(&opts, "fflags", "nobuffer", 0);
    av_dict_set(&opts, "max_delay", "500000", 0);
    av_dict_set(&opts, "stimeout", "5000000", 0);
    av_dict_set(&opts, "reorder_queue_size", "0", 0);

    int ret = avformat_open_input(
            &fmt,
            url,
            nullptr,
            &opts);
    if (ret < 0) {
        char errbuf[256];

        av_strerror(
                ret,
                errbuf,
                sizeof(errbuf));

        std::string msg =
                "open failed: ";

        msg += "ret=" + std::to_string(ret);

        msg += errbuf;

        env->ReleaseStringUTFChars(url_, url);

        return env->NewStringUTF(msg.c_str());
    }
    ret = avformat_find_stream_info(fmt, nullptr);

    if (ret < 0) {
        avformat_close_input(&fmt);
        env->ReleaseStringUTFChars(url_, url);
        return env->NewStringUTF("find stream info failed");
    }

    std::string result;

    for (unsigned i = 0; i < fmt->nb_streams; i++) {

        AVStream *stream = fmt->streams[i];

        if (stream->codecpar->codec_type ==
                AVMEDIA_TYPE_VIDEO) {

            auto *codecpar = stream->codecpar;

            result += "Video\n";

            result += "Codec: ";
            result += avcodec_get_name(codecpar->codec_id);
            result += "\n";

            result += "Width: ";
            result += std::to_string(codecpar->width);
            result += "\n";

            result += "Height: ";
            result += std::to_string(codecpar->height);
            result += "\n";

            if (stream->avg_frame_rate.den != 0) {
                double fps =
                        av_q2d(stream->avg_frame_rate);

                result += "FPS: ";
                result += std::to_string(fps);
                result += "\n";
            }
        }
    }

    avformat_close_input(&fmt);

    env->ReleaseStringUTFChars(url_, url);

    return env->NewStringUTF(result.c_str());
}
