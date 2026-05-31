#include <jni.h>
#include <android/log.h>

#define LOG_TAG "JNI"

#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define LOGW(...) __android_log_print(ANDROID_LOG_WARN, LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)


extern "C"
JNIEXPORT jstring JNICALL
Java_dev_dl_demoapp_core_jni_NativeLib_helloFromJni(JNIEnv *env, jobject) {
    return env->NewStringUTF("Hello JNI");
}

int add(int a, int b)
{
    return a + b;
}

extern "C"
JNIEXPORT jint JNICALL
Java_dev_dl_demoapp_core_jni_NativeLib_addFromJni(JNIEnv *env, jobject, jint a, jint b) {
    LOGI("You're logging from jni.");
    LOGW("This warning use string formating, arg: %s", "I am an arg");
    return add(a, b);
}