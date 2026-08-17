package dev.dl.demoapp.core.jni

import android.util.Log

object NativeLib {
    init {
        System.loadLibrary("NativeLib")

//        Log.i("JNI", "NativeLib loaded")
    }


//    external fun getFFmpegVersion(): Long

//    external fun probeRtsp(url: String): String

    external fun probeMedia(url: String): NativeResult<MediaInfo>

    external fun testDecoderOpen(
        url: String,
    ): NativeResult<Boolean>
}