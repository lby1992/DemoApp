package dev.dl.demoapp.player

import android.graphics.SurfaceTexture
import android.os.Bundle
import android.util.Log
import android.view.Surface
import android.view.TextureView
import androidx.activity.ComponentActivity
import dev.dl.demoapp.R
import dev.dl.demoapp.core.jni.NativeLib
import dev.dl.demoapp.core.jni.NativePlayer
import dev.dl.demoapp.core.jni.StreamType
import java.io.File

class VideoPlayerActivity : ComponentActivity() {

    private lateinit var textureView: TextureView

    private val player: NativePlayer by lazy {
        NativePlayer().apply {
            init(applicationContext)
            create()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_player)

        textureView = findViewById(R.id.textureView)

        textureView.surfaceTextureListener = object : TextureView.SurfaceTextureListener {
            override fun onSurfaceTextureAvailable(
                surfaceTexture: SurfaceTexture,
                width: Int,
                height: Int
            ) {
                val surface = Surface(surfaceTexture)

                player.setSurface(surface)
            }

            override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
                return true
            }

            override fun onSurfaceTextureSizeChanged(
                surface: SurfaceTexture,
                width: Int,
                height: Int
            ) {
            }

            override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {

            }
        }


//        val rtspUri = "rtsp://9627b0bf2a7b.entrypoint.cloud.wowza.com:1935/app-p5260J38/66abe4b9_stream1"
//        val mediaItem = MediaItem.fromUri(rtspUri)
//        player.setMediaItem(mediaItem)
//
//        player.prepare()
//
//        player.play()

//        val rtspUrl = "rtsp://10.0.2.2:8554/live"
//        val result = NativeLib.probeRtsp(rtspUrl)

//        val mp4Url = "https://samplelib.com/mp4/sample-5s.mp4"

        val file = File(applicationContext.filesDir, "sample-5s.mp4")
        player.open(file.absolutePath)
//        val result = NativeLib.probeMedia(file.absolutePath)
//        if (result.success) {
//            Log.i(TAG, "success")
//            result.data?.also {
//                Log.i(TAG, "durations: ${it.durationMs}")
//
//                it.streams.forEach { stream ->
//                    when (stream.type) {
//                        StreamType.VIDEO -> Log.i(
//                            TAG,
//                            "Stream ${stream.index}, codec: ${stream.codec}, ${stream.width}x${stream.height}, FPS: ${stream.fps}, bitrate: ${stream.bitrate}"
//                        )
//
//                        StreamType.AUDIO -> Log.i(
//                            TAG,
//                            "Stream ${stream.index}, codec: ${stream.codec}, sample rate: ${stream.sampleRate}, channels: ${stream.channels}"
//                        )
//
//                        else -> {}
//                    }
//
//                }
//            }
//        } else {
//            Log.i(TAG, "Failed to probe the resource: [${result.errorCode}]${result.errorMessage}")
//        }
//        val result2 = NativeLib.testDecoderOpen(file.absolutePath)
//        Log.i(
//            TAG,
//            "decoder open: ${result2.success} - ${result2.errorCode} - ${result2.errorMessage}"
//        )
    }

    override fun onStart() {
        super.onStart()

        player.play()
    }

    override fun onStop() {
        super.onStop()

        player.stop()
    }

    override fun onDestroy() {
        super.onDestroy()

        player.release()
    }

    companion object {
        private const val TAG = "VideoPlayerActivity"
    }
}