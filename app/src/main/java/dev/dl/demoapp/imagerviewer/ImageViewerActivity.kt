package dev.dl.demoapp.imagerviewer

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import dev.dl.demoapp.R

class ImageViewerActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_image_viewer)
    }
}