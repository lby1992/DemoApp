#include "player/NativePlayer.h"
#include "utils/AssetManagerHolder.h"

#include <jni.h>

#include <android/native_window_jni.h>
#include <android/asset_manager.h>
#include <android/asset_manager_jni.h>


extern "C"
JNIEXPORT jlong JNICALL
Java_dev_dl_demoapp_core_jni_NativePlayer_nativeCreate(JNIEnv *env, jobject thiz) {
    auto *player = new NativePlayer();

    return reinterpret_cast<jlong>(player);
}

extern "C"
JNIEXPORT void JNICALL
Java_dev_dl_demoapp_core_jni_NativePlayer_nativeSetSurface(JNIEnv *env, jobject thiz, jlong handle, jobject surface) {
    auto *player = reinterpret_cast<NativePlayer *>(handle);

    ANativeWindow *window = ANativeWindow_fromSurface(env, surface);

    if (window == nullptr) {
        return;
    }

    player->setSurface(window);
}

extern "C"
JNIEXPORT void JNICALL
Java_dev_dl_demoapp_core_jni_NativePlayer_nativeRelease(JNIEnv *env, jobject thiz, jlong handle) {
    auto *player = reinterpret_cast<NativePlayer *>(handle);

    if (player) {
        player->release();

        delete player;
    }
}

extern "C"
JNIEXPORT jboolean JNICALL
Java_dev_dl_demoapp_core_jni_NativePlayer_nativeOpen(JNIEnv *env, jobject thiz, jlong handle, jstring url_) {
    auto *player = reinterpret_cast<NativePlayer *>(handle);

    if (player == nullptr) {
        return false;
    }

    const char *url = env->GetStringUTFChars(url_, nullptr);

    bool result = player->open(url);

    env->ReleaseStringUTFChars(url_, url);

    return result;
}

extern "C"
JNIEXPORT void JNICALL
Java_dev_dl_demoapp_core_jni_NativePlayer_nativePlay(JNIEnv *env, jobject thiz, jlong handle) {
    auto *player = reinterpret_cast<NativePlayer *>(handle);

    if (player == nullptr) {
        return;
    }

    player->play();
}

extern "C"
JNIEXPORT void JNICALL
Java_dev_dl_demoapp_core_jni_NativePlayer_nativeStop(JNIEnv *env, jobject thiz, jlong handle) {
    auto *player = reinterpret_cast<NativePlayer *>(handle);

    if (player == nullptr) {
        return;
    }

    player->stop();
}

extern "C"
JNIEXPORT void JNICALL
Java_dev_dl_demoapp_core_jni_NativePlayer_nativeInit(JNIEnv *env, jobject thiz, jobject assetManager) {
    AAssetManager *mgr = AAssetManager_fromJava(
            env,
            assetManager
    );

    AssetManagerHolder::init(mgr);
}