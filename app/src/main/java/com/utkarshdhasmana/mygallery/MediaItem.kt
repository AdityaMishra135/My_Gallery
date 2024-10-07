import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class MediaItem(
    val id: Long,
    val filePath: String,
    val displayName: String,
    val mimeType: String,
    val size: Long, // Size in bytes
    val dateModified: Long // Date modified in milliseconds
) {
    fun isVideo(): Boolean {
        return mimeType.startsWith("video/")
    }

    fun isImage(): Boolean {
        return mimeType.startsWith("image/")
    }

    // Optional: Additional methods to format size or date for display
    fun getFormattedSize(): String {
        return when {
            size >= 1024 * 1024 -> "${size / (1024 * 1024)} MB" // Convert to MB
            size >= 1024 -> "${size / 1024} KB" // Convert to KB
            else -> "$size bytes" // Return in bytes
        }
    }

    fun getFormattedDate(): String {
        // Convert the dateModified from milliseconds to a readable format
        val date = Date(dateModified * 1000) // Convert to milliseconds
        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return formatter.format(date)
    }
}
