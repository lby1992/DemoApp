package dev.dl.demoapp.core.jni

class NativeResult<T> {
    var success = false

    var errorCode = 0

    var errorMessage: String? = null

    var data: T? = null
}