package com.utkarshdhasmana.mygallery

import MediaItem
import android.content.ContentResolver
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen

import androidx.recyclerview.widget.GridLayoutManager
import com.utkarshdhasmana.mygallery.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var adapterMedia: MediaAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val splashScreen = installSplashScreen()

        // Keep the splash screen on until the content is ready
        splashScreen.setKeepOnScreenCondition {
            // Display for a duration
            Thread.sleep(2000) // This is just for demonstration; use coroutines in real apps
            false // Return false to hide the splash screen
        }
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapterMedia = MediaAdapter()

        if (PermissionManager.hasPermissions(this)) {
            loadInRecycler()
        } else {
            PermissionManager.requestPermission(this)
        }

        setupButtons()
    }

    private fun setupButtons() {
        val btnSortDateAsc: Button = findViewById(R.id.btn_sort_date_asc)
        val btnSortDateDesc: Button = findViewById(R.id.btn_sort_date_desc)
        val btnSortSizeAsc: Button = findViewById(R.id.btn_sort_size_asc)
        val btnSortSizeDesc: Button = findViewById(R.id.btn_sort_size_desc)


        btnSortDateAsc.setOnClickListener { loadInRecycler("date", true) }
        btnSortDateDesc.setOnClickListener { loadInRecycler("date", false) }
        btnSortSizeAsc.setOnClickListener { loadInRecycler("size", true) }
        btnSortSizeDesc.setOnClickListener { loadInRecycler("size", false) }


        adapterMedia.setOnItemClickListener { media -> openMedia(media) }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        PermissionManager.onRequestPermissionsResult(
            requestCode,
            grantResults,
            { loadInRecycler() },
            { Toast.makeText(this, "Permissions are required to access media items.", Toast.LENGTH_SHORT).show() }
        )
    }

    private fun loadInRecycler(sortBy: String = "date", ascending: Boolean = true) {
        val mediaItems = loadMediaItems()
        val sortedMediaItems = sortMediaItems(mediaItems, sortBy, ascending)
        adapterMedia.submitList(sortedMediaItems)
        binding.recyclerView.apply {
            adapter = adapterMedia
            layoutManager = GridLayoutManager(this@MainActivity, 4)
        }
    }

    private fun openMedia(mediaItem: MediaItem) {
        val intent = if (mediaItem.isVideo()) {
            Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI.buildUpon().appendPath(mediaItem.id.toString()).build(),
                    "video/*"
                )
            }
        } else {
            Intent(this, ImageViewerActivity::class.java).apply {
                putExtra(ImageViewerActivity.EXTRA_IMAGE_PATH, mediaItem.filePath)
            }
        }
        startActivity(intent)
    }

    private fun loadMediaItems(): List<MediaItem> {
        val mediaList = mutableListOf<MediaItem>()

        val projection = arrayOf(
            MediaStore.Files.FileColumns._ID,
            MediaStore.Files.FileColumns.DATA,
            MediaStore.Files.FileColumns.DISPLAY_NAME,
            MediaStore.Files.FileColumns.MIME_TYPE,
            MediaStore.Files.FileColumns.SIZE,
            MediaStore.Files.FileColumns.DATE_MODIFIED
        )
        val selection = "${MediaStore.Files.FileColumns.MEDIA_TYPE}=? OR ${MediaStore.Files.FileColumns.MEDIA_TYPE}=?"
        val selectionArgs = arrayOf(
            MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE.toString(),
            MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO.toString()
        )
        val sortOrder = "${MediaStore.Files.FileColumns.DATE_MODIFIED} DESC"

        val cursor = contentResolver.query(
            MediaStore.Files.getContentUri("external"),
            projection,
            selection,
            selectionArgs,
            sortOrder
        )

        cursor?.use {
            val idColumn = it.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID)
            val filePathColumn = it.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA)
            val displayNameColumn = it.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DISPLAY_NAME)
            val mimeTypeColumn = it.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MIME_TYPE)
            val sizeColumn = it.getColumnIndexOrThrow(MediaStore.Files.FileColumns.SIZE)
            val dateModifiedColumn = it.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATE_MODIFIED)

            while (it.moveToNext()) {
                val id = it.getLong(idColumn)
                val filePath = it.getString(filePathColumn)
                val displayName = it.getString(displayNameColumn)
                val mimeType = it.getString(mimeTypeColumn)
                val size = it.getLong(sizeColumn)
                val dateModified = it.getLong(dateModifiedColumn)

                mediaList.add(MediaItem(id, filePath, displayName, mimeType, size, dateModified))
            }
        }

        return mediaList
    }

    private fun sortMediaItems(mediaItems: List<MediaItem>, sortBy: String, ascending: Boolean): List<MediaItem> {
        return when (sortBy) {
            "date" -> if (ascending) {
                mediaItems.sortedBy { it.dateModified }
            } else {
                mediaItems.sortedByDescending { it.dateModified }
            }
            "size" -> if (ascending) {
                mediaItems.sortedBy { it.size }
            } else {
                mediaItems.sortedByDescending { it.size }
            }
            else -> mediaItems
        }
    }

    private fun filterMediaItemsByType(type: MediaType) {
        val mediaItems = loadMediaItems()
        val filteredMediaItems = when (type) {
            MediaType.IMAGE -> mediaItems.filter { it.isImage() }
            MediaType.VIDEO -> mediaItems.filter { it.isVideo() }
            MediaType.ALL -> mediaItems
        }
        adapterMedia.submitList(filteredMediaItems)
    }
}

enum class MediaType {
    IMAGE,
    VIDEO,
    ALL
}
