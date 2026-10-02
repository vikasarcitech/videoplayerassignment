package com.example.videoplayerassignment.data.model

import com.google.gson.annotations.SerializedName

data class SectionItem(
    @SerializedName("id") val id: Int = 0,
    @SerializedName("title") val title: String? = null,
    @SerializedName("short_title") val shortTitle: String? = null,
    @SerializedName("description") val description: String? = null,
    @SerializedName("screen_layout") val screenLayout: String? = null,
    @SerializedName("sort_order") val sortOrder: Int = 0,
    @SerializedName("data") val data: List<VideoItem> = emptyList()
)
