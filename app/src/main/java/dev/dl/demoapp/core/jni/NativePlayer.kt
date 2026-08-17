package dev.dl.demoapp.core.jni

import android.content.Context
import android.content.res.AssetManager
import android.view.Surface

class NativePlayer {
    init {
        System.loadLibrary("NativeLib")
    }

    private var handle: Long = 0


    private external fun nativeCreate(): Long


    private external fun nativeOpen(
        handle: Long,
        url: String
    ): Boolean


    private external fun nativeSetSurface(
        handle: Long,
        surface: Surface
    )


    private external fun nativePlay(
        handle: Long
    )


    private external fun nativeStop(
        handle: Long
    )


    private external fun nativeRelease(
        handle: Long
    )

    private external fun nativeInit(
        assetManager: AssetManager,
    )

    fun init(context: Context) {
        nativeInit(context.assets)
    }


    fun create() {
        handle = nativeCreate()
    }


    fun open(url: String): Boolean {
        return nativeOpen(handle, url)
    }

    fun setSurface(surface: Surface) {
        nativeSetSurface(
            handle,
            surface
        )
    }


    fun play() {
        nativePlay(handle)
    }


    fun stop() {
        nativeStop(handle)
    }


    fun release() {
        nativeRelease(handle)
    }
}