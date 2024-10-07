package com.utkarshdhasmana.mygallery

import MediaItem

data class Album(
    var title: String,
    val mediaItems: MutableList<MediaItem> = mutableListOf()
)
