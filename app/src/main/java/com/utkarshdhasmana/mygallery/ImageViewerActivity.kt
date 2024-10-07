package com.utkarshdhasmana.mygallery

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide

class ImageViewerActivity : AppCompatActivity() {
    private lateinit var imageView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_viewer)

        imageView = findViewById(R.id.imageView)

        // Get the image path from the intent
        val imagePath = intent.getStringExtra(EXTRA_IMAGE_PATH)

        if (imagePath != null) {
            // Load the image using Glide
            Glide.with(this)
                .load(imagePath)
                .into(imageView)
        }
    }

    companion object {
        const val EXTRA_IMAGE_PATH = "extra_image_path"
    }
}
