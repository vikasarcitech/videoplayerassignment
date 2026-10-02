package com.example.videoplayerassignment.data.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class VideoItem(
    @SerializedName("id") val id: Int = 0,
    @SerializedName("type_id") val typeId: Int = 0,
    @SerializedName("video_type") val videoType: Int = 0,
    @SerializedName("name") val name: String? = null,
    @SerializedName("description") val description: String? = null,
    @SerializedName("thumbnail") val thumbnail: String? = null,
    @SerializedName("landscape") val landscape: String? = null,
    @SerializedName("video_320") val video320: String? = null,
    @SerializedName("video_480") val video480: String? = null,
    @SerializedName("video_720") val video720: String? = null,
    @SerializedName("video_1080") val video1080: String? = null,
    @SerializedName("trailer_url") val trailerUrl: String? = null,
    @SerializedName("screen_layout") val screenLayout: String? = null,
    @SerializedName("category_name") val categoryName: String? = null,
    @SerializedName("total_view") val totalView: Int = 0,
    @SerializedName("total_like") val totalLike: Int = 0,
    @SerializedName("is_premium") val isPremium: Int = 0
) : Serializable {

    fun getBestPlaybackUrl(): String {
        return when {
            !video1080.isNullOrBlank() -> video1080
            !video720.isNullOrBlank() -> video720
            !video480.isNullOrBlank() -> video480
            !video320.isNullOrBlank() -> video320
            !trailerUrl.isNullOrBlank() -> trailerUrl
            else -> ""
        }
    }

    fun getPosterImage(): String {
        return when {
            !landscape.isNullOrBlank() -> landscape
            !thumbnail.isNullOrBlank() -> thumbnail
            else -> ""
        }
    }

    fun getQualityMap(): Map<String, String> {
        val map = mutableMapOf<String, String>()
        if (!video1080.isNullOrBlank()) map["1080p (HD)"] = video1080
        if (!video720.isNullOrBlank()) map["720p (HD)"] = video720
        if (!video480.isNullOrBlank()) map["480p (SD)"] = video480
        if (!video320.isNullOrBlank()) map["320p (Low)"] = video320
        if (!trailerUrl.isNullOrBlank() && map.isEmpty()) map["Trailer Stream"] = trailerUrl
        return map
    }
}
