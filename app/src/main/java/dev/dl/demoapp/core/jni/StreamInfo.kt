package dev.dl.demoapp.core.jni

class StreamInfo {
    var index = -1

    var type = StreamType.UNKNOWN

    var codec = ""

    var codecId = 0

    // ========= Video =============
    var width = 0
    var height = 0
    var fps = 0.0
    var bitrate = 0L

    // ============ Audio ==============
    var sampleRate = 0
    var channels = 0
}

object StreamType {
    const val UNKNOWN = -1

    const val VIDEO = 0

    const val AUDIO = 1

    const val DATA = 2

    const val SUBTITLE = 3
}