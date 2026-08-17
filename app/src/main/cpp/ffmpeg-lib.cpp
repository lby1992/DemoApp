////
//// Created by libiy on 5/31/2026.
////
//#include <jni.h>
//#include <string>
//#include <android/log.h>
//
//
//#define LOG_TAG "NATIVE_FFMPEG"
//
//#define LOGV(...) __android_log_print(ANDROID_LOG_VERBOSE, LOG_TAG, __VA_ARGS__)
//#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)
//#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
//#define LOGW(...) __android_log_print(ANDROID_LOG_WARN, LOG_TAG, __VA_ARGS__)
//#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)
//
//extern "C"
//{
//#include "third_party/ffmpeg/include/libavformat/avformat.h"
//#include "third_party/ffmpeg/include/libavcodec/avcodec.h"
//#include "third_party/ffmpeg/include/libavutil/log.h"
////#include "nlohmann/json.hpp"
//}
//
//static void ffmpegLogCallback(
//        void *ptr,
//        int level,
//        const char *fmt,
//        va_list vl) {
//
//    char buffer[1024];
//
//    vsnprintf(
//            buffer,
//            sizeof(buffer),
//            fmt,
//            vl);
//
//    LOGD("%s", buffer);
//}
//
//
//std::string format_error(int ret) {
//    char errbuf[256];
//
//    av_strerror(
//            ret,
//            errbuf,
//            sizeof(errbuf)
//    );
//    std::string msg = "Open failed: ";
//    msg += "ret=" + std::to_string(ret);
//    msg += errbuf;
//
//    return msg;
//}
//
//JNIEXPORT jint JNICALL
//JNI_OnLoad(
//        JavaVM *vm,
//        void *reserved) {
//
//    av_log_set_level(AV_LOG_TRACE);
//
//    av_log_set_callback(
//            ffmpegLogCallback);
//
//    LOGI("FFmpeg log initialized.");
//
//    return JNI_VERSION_1_6;
//}
//
//extern "C"
//JNIEXPORT jlong JNICALL
//Java_dev_dl_demoapp_core_jni_NativeLib_getFFmpegVersion(JNIEnv *env, jobject thiz) {
//    return avformat_version();
//}
//
//extern "C"
//JNIEXPORT jobject JNICALL
//Java_dev_dl_demoapp_core_jni_NativeLib_probeMedia(JNIEnv *env, jobject thiz, jstring url_) {
//    const char *url = env->GetStringUTFChars(url_, nullptr);
//
//    AVFormatContext *fmt = nullptr;
//
////    AVDictionary *opts = nullptr;
//
//    jclass resultCls = env->FindClass("dev/dl/demoapp/core/jni/NativeResult");
//
//    jmethodID resultConstructor = env->GetMethodID(resultCls, "<init>", "()V");
//
//    jobject resultObj = env->NewObject(resultCls, resultConstructor);
//
//    jfieldID successField = env->GetFieldID(resultCls, "success", "Z");
//    jfieldID errorCodeField = env->GetFieldID(resultCls, "errorCode", "I");
//    jfieldID errorMsgField = env->GetFieldID(resultCls, "errorMessage", "Ljava/lang/String;");
//    jfieldID dataFiled = env->GetFieldID(resultCls, "data", "Ljava/lang/Object;");
//
//    // Open stream
//    int ret = avformat_open_input(
//            &fmt,
//            url,
//            nullptr,
//            nullptr
//    );
//    env->ReleaseStringUTFChars(url_, url);
//
//    if (ret < 0) {
//        env->SetBooleanField(resultObj, successField, JNI_FALSE);
//        env->SetIntField(resultObj, errorCodeField, ret);
//
//        std::string error_msg = format_error(ret);
//
//        env->SetObjectField(resultObj, errorMsgField, env->NewStringUTF(error_msg.c_str()));
//
//        return resultObj;
//    }
//
//    // Read Stream info
//    ret = avformat_find_stream_info(
//            fmt,
//            nullptr
//    );
//    if (ret < 0) {
//        env->SetBooleanField(resultObj, successField, JNI_FALSE);
//        env->SetIntField(resultObj, errorCodeField, ret);
//        std::string msg = format_error(ret);
//        env->SetObjectField(resultObj, errorMsgField, env->NewStringUTF(msg.c_str()));
//
//        avformat_close_input(&fmt);
//
//        return resultObj;
//    }
//
//    jclass mediaCls = env->FindClass("dev/dl/demoapp/core/jni/MediaInfo");
//    jmethodID mediaConstructor = env->GetMethodID(mediaCls, "<init>", "()V");
//    jobject mediaObj = env->NewObject(mediaCls, mediaConstructor);
//
//    jfieldID durationField = env->GetFieldID(mediaCls, "durationMs", "J");
//    jfieldID streamsField = env->GetFieldID(mediaCls, "streams", "Ljava/util/List;");
//
//
//    long durationInMs = fmt->duration * 1000 / AV_TIME_BASE;
//    env->SetLongField(mediaObj, durationField, durationInMs);
//
//    jclass arrayListCls = env->FindClass("java/util/ArrayList");
//    jmethodID arrayListConstructor = env->GetMethodID(arrayListCls, "<init>", "()V");
//    jobject streamList = env->NewObject(arrayListCls, arrayListConstructor);
//    jmethodID addMethod = env->GetMethodID(arrayListCls, "add", "(Ljava/lang/Object;)Z");
//
//
//    int streamCount = fmt->nb_streams;
//
//    for (unsigned int i = 0; i < streamCount; i++) {
//        AVStream *stream = fmt->streams[i];
//
//        auto *codecpar = stream->codecpar;
//        jobject streamObj;
//        const char *codec;
//        double fps;
//
//        switch (stream->codecpar->codec_type) {
//            case AVMEDIA_TYPE_UNKNOWN:
//                LOGW("Unknown stream type.");
//                break;
//            case AVMEDIA_TYPE_VIDEO:
//                LOGI("Found video stream.");
//
//                streamObj = env->NewObject(streamCls, streamConstructor);
//                env->SetIntField(streamObj, indexField, i);
//                env->SetIntField(streamObj, typeField, 0);
//                codec = avcodec_get_name(codecpar->codec_id);
//                env->SetObjectField(streamObj, codecField, env->NewStringUTF(codec));
////                env->SetIntField(streamObj, codecIdField, codecpar->codec_id);
//                env->SetIntField(streamObj, widthField, codecpar->width);
//                env->SetIntField(streamObj, heightField, codecpar->height);
//
//                fps = 0;
//                if (stream->avg_frame_rate.den != 0) {
//                    fps = av_q2d(stream->avg_frame_rate);
//                }
//                env->SetDoubleField(streamObj, fpsField, fps);
//                env->SetLongField(streamObj, bitrateField, codecpar->bit_rate);
//
//                env->CallBooleanMethod(streamList, addMethod, streamObj);
//                break;
//            case AVMEDIA_TYPE_AUDIO:
//                LOGI("Found audio stream.");
//                streamObj = env->NewObject(streamCls, streamConstructor);
//                env->SetIntField(streamObj, indexField, i);
//                env->SetIntField(streamObj, typeField, 1);
//                codec = avcodec_get_name(codecpar->codec_id);
//                env->SetObjectField(streamObj, codecField, env->NewStringUTF(codec));
////                env->SetIntField(streamObj, codecIdField, codecpar->codec_id);
//                env->SetIntField(streamObj, sampleRateField, codecpar->sample_rate);
//                env->SetIntField(streamObj, channelsField, codecpar->channels);
//
//                env->CallBooleanMethod(streamList, addMethod, streamObj);
//                break;
//            case AVMEDIA_TYPE_DATA:
//                LOGI("Found data stream.");
//                break;
//            case AVMEDIA_TYPE_SUBTITLE:
//                LOGI("Found subtitle stream.");
//                break;
//            case AVMEDIA_TYPE_ATTACHMENT:
//                LOGI("Found attachment stream.");
//                break;
//            case AVMEDIA_TYPE_NB:
//                LOGI("Found NB stream.");
//                break;
//        }
//    }
//
//    avformat_close_input(&fmt);
//
//    env->SetObjectField(mediaObj, streamsField, streamList);
//
//    env->SetBooleanField(resultObj, successField, JNI_TRUE);
//    env->SetObjectField(resultObj, dataFiled, mediaObj);
//
//    return resultObj;
//}
//
//
//extern "C"
//JNIEXPORT jstring JNICALL
//Java_dev_dl_demoapp_core_jni_NativeLib_probeRtsp(JNIEnv *env, jobject thiz, jstring url_) {
//    const char *url = env->GetStringUTFChars(url_, nullptr);
//    AVFormatContext *fmt = nullptr;
//
////    int ret = avformat_open_input(&fmt, url, nullptr, nullptr);
//    AVDictionary *opts = nullptr;
//
//    av_dict_set(&opts, "rtsp_transport", "tcp", 0);
//    av_dict_set(&opts, "fflags", "nobuffer", 0);
//    av_dict_set(&opts, "max_delay", "500000", 0);
//    av_dict_set(&opts, "stimeout", "5000000", 0);
//    av_dict_set(&opts, "reorder_queue_size", "0", 0);
//
//    int ret = avformat_open_input(
//            &fmt,
//            url,
//            nullptr,
//            &opts);
//    if (ret < 0) {
//        char errbuf[256];
//
//        av_strerror(
//                ret,
//                errbuf,
//                sizeof(errbuf));
//
//        std::string msg =
//                "open failed: ";
//
//        msg += "ret=" + std::to_string(ret);
//
//        msg += errbuf;
//
//        env->ReleaseStringUTFChars(url_, url);
//
//        return env->NewStringUTF(msg.c_str());
//    }
//    ret = avformat_find_stream_info(fmt, nullptr);
//
//    if (ret < 0) {
//        avformat_close_input(&fmt);
//        env->ReleaseStringUTFChars(url_, url);
//        return env->NewStringUTF("find stream info failed");
//    }
//
//    std::string result;
//
//    for (unsigned i = 0; i < fmt->nb_streams; i++) {
//
//        AVStream *stream = fmt->streams[i];
//
//        if (stream->codecpar->codec_type ==
//                AVMEDIA_TYPE_VIDEO) {
//
//            auto *codecpar = stream->codecpar;
//
//            result += "Video\n";
//
//            result += "Codec: ";
//            result += avcodec_get_name(codecpar->codec_id);
//            result += "\n";
//
//            result += "Width: ";
//            result += std::to_string(codecpar->width);
//            result += "\n";
//
//            result += "Height: ";
//            result += std::to_string(codecpar->height);
//            result += "\n";
//
//            if (stream->avg_frame_rate.den != 0) {
//                double fps =
//                        av_q2d(stream->avg_frame_rate);
//
//                result += "FPS: ";
//                result += std::to_string(fps);
//                result += "\n";
//            }
//        }
//    }
//
//    avformat_close_input(&fmt);
//
//    env->ReleaseStringUTFChars(url_, url);
//
//    return env->NewStringUTF(result.c_str());
//}
//
//extern "C"
//JNIEXPORT jobject JNICALL
//Java_dev_dl_demoapp_core_jni_NativeLib_probe(JNIEnv *env, jobject thiz, jstring jUrl) {
//    const char *url = env->GetStringUTFChars(jUrl, nullptr);
//
//    AVFormatContext *fmt = nullptr;
//
//    int ret = avformat_open_input(&fmt, url, nullptr, nullptr);
//
//    if (ret < 0) {
//        env->ReleaseStringUTFChars(jUrl, url);
//        return nullptr;
//    }
//
//    ret = avformat_find_stream_info(fmt, nullptr);
//
//    if (ret < 0) {
//        avformat_close_input(&fmt);
//        env->ReleaseStringUTFChars(jUrl, url);
//        return nullptr;
//    }
//
//    int videoStream = -1;
//    for (int i = 0; i < fmt->nb_streams; i++) {
//        if (fmt->streams[i]->codecpar->codec_type == AVMEDIA_TYPE_VIDEO) {
//            videoStream = i;
//            break;
//        }
//    }
//
//    if (videoStream < 0) {
//        avformat_close_input(&fmt);
//        env->ReleaseStringUTFChars(jUrl, url);
//        return nullptr;
//    }
//
//    AVStream *stream = fmt->streams[videoStream];
//    AVCodecParameters *codecPar = stream->codecpar;
//
//    return nullptr;
//}
