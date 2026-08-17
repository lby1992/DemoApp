package dev.dl.demoapp.player

import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.graphics.SurfaceTexture
import android.os.Bundle
import android.util.Log
import android.view.Surface
import android.view.TextureView
import android.view.ViewGroup
import android.widget.Button
import androidx.activity.ComponentActivity
import androidx.core.net.toUri
import dev.dl.demoapp.R
import org.videolan.libvlc.LibVLC
import org.videolan.libvlc.Media
import org.videolan.libvlc.MediaPlayer
import org.videolan.libvlc.util.VLCVideoLayout

class VLCVideoPlayerActivity : ComponentActivity() {

    private lateinit var textureView: TextureView
    private lateinit var loadingLayout: ViewGroup

    private lateinit var libVLC: LibVLC
    private lateinit var mediaPlayer: MediaPlayer

    private var surface: Surface? = null

    private val surfaceTextureListener by lazy {
        object : TextureView.SurfaceTextureListener {
            override fun onSurfaceTextureAvailable(
                surfaceTexture: SurfaceTexture,
                width: Int,
                height: Int
            ) {
                surface = Surface(surfaceTexture)
                    .also {
                        mediaPlayer.vlcVout.setVideoSurface(it, null)
                    }
                mediaPlayer.vlcVout.setWindowSize(width, height)
                mediaPlayer.vlcVout.attachViews()

                play()
            }

            override fun onSurfaceTextureDestroyed(surfaceTexture: SurfaceTexture): Boolean {
                mediaPlayer.stop()
                mediaPlayer.vlcVout.detachViews()

                surface?.release()
                surface = null

                return true
            }

            override fun onSurfaceTextureSizeChanged(
                surfaceTexture: SurfaceTexture,
                width: Int,
                height: Int
            ) {
                mediaPlayer.vlcVout.setWindowSize(width, height)
            }

            override fun onSurfaceTextureUpdated(surfaceTexture: SurfaceTexture) {
//                Log.i(TAG, "onSurfaceTextureUpdated: ${surfaceTexture.timestamp}")
            }
        }
    }

    private var isRotated = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_vlc_video_player)

        textureView = findViewById(R.id.textureView)
        textureView.surfaceTextureListener = surfaceTextureListener
        loadingLayout = findViewById(R.id.loadingLayout)

        findViewById<Button>(R.id.rotateButton).setOnClickListener {
            val lp = textureView.layoutParams
            if (isRotated) {

            } else {

            }
//            requestedOrientation =
//                if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
//                    ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
//                } else {
//                    ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
//                }
        }

        initPlayer()
    }

    private fun initPlayer() {
        libVLC = LibVLC(
            this,
            arrayListOf(
                "--no-drop-late-frames",
                "--no-skip-frames",
                "--network-caching=1000",
            )
        )

        mediaPlayer = MediaPlayer(libVLC)

        mediaPlayer.setEventListener { event ->
//            Log.d(
//                TAG,
//                "event=${event.type}, buffering=${event.buffering}"
//            )

            when (event.type) {
                MediaPlayer.Event.Opening ->
                    Log.d(TAG, "Opening")

                MediaPlayer.Event.Buffering ->
                    Log.d(TAG, "Buffering ${event.buffering}")

                MediaPlayer.Event.Playing ->
                    Log.d(TAG, "Playing")

                MediaPlayer.Event.EndReached ->
                    Log.d(TAG, "EndReached")

                MediaPlayer.Event.EncounteredError ->
                    Log.e(TAG, "EncounteredError")
            }
        }

        mediaPlayer.aspectRatio = null
        mediaPlayer.scale = 0f
    }

    private fun play() {
//        val url = "https://test-streams.mux.dev/x36xhzz/x36xhzz.m3u8"
        val url = "https://file-examples.com/storage/fe2bc5b1ac6a4a6979e3f2c/2017/04/file_example_MP4_640_3MG.mp4"
        val media = Media(
            libVLC,
            url.toUri()
        )

        mediaPlayer.media = media
        media.release()

        mediaPlayer.play()
    }


    private fun syncPlayerLayout() {
        val w = textureView.width
        val h = textureView.height

        mediaPlayer.vlcVout.setWindowSize(w, h)

    }
    
    companion object {
        private const val TAG = "VLCVideoPlayerActivity"
    }
}