package com.utkarshdhasmana.mygallery

import MediaItem
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.utkarshdhasmana.mygallery.databinding.ItemGalleryBinding

class MediaAdapter : ListAdapter<MediaItem, MediaAdapter.MediaViewHolder>(MediaDiffCallback()) {

    inner class MediaViewHolder(private val binding: ItemGalleryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(mediaItem: MediaItem) {
            Glide.with(itemView)
                .load(mediaItem.filePath)
                .centerCrop()
                .placeholder(R.drawable.pixel)
                .into(binding.imageview)

            // Show or hide the duration based on media type
            if (mediaItem.isImage()) {
                binding.tvDuration.visibility = View.GONE
            } else if (mediaItem.isVideo()) {
                binding.tvDuration.visibility = View.VISIBLE
                binding.tvDuration.text = getVideoDurationAsString(mediaItem.filePath)
            }

            // Click listener for opening media
            binding.root.setOnClickListener {
                onItemClickListener?.invoke(mediaItem)
            }


        }
    }

    // Listener for item clicks
    private var onItemClickListener: ((MediaItem) -> Unit)? = null

    // Method to set the click listener for media items
    fun setOnItemClickListener(listener: (MediaItem) -> Unit) {
        onItemClickListener = listener
    }

    // Create a new view holder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MediaViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemGalleryBinding.inflate(inflater, parent, false)
        return MediaViewHolder(binding)
    }

    // Bind the media item to the view holder
    override fun onBindViewHolder(holder: MediaViewHolder, position: Int) {
        val mediaItem = getItem(position)
        holder.bind(mediaItem)
    }

    // A method to format video duration as a string
    private fun getVideoDurationAsString(filePath: String): String {
        // Implement your logic to get video duration from filePath
        return "00:00" // Placeholder for demonstration; replace with actual duration logic
    }
}
