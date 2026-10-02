package com.example.videoplayerassignment

import com.example.videoplayerassignment.data.model.SectionResponse
import com.example.videoplayerassignment.data.model.VideoItem
import com.google.gson.Gson
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun testVideoItemBestPlaybackUrlSelection() {
        val itemWith1080p = VideoItem(
            video1080 = "https://example.com/1080.mp4",
            video720 = "https://example.com/720.mp4",
            video320 = "https://example.com/320.mp4"
        )
        assertEquals("https://example.com/1080.mp4", itemWith1080p.getBestPlaybackUrl())

        val itemWith320pOnly = VideoItem(
            video1080 = "",
            video720 = null,
            video320 = "https://example.com/320.mp4"
        )
        assertEquals("https://example.com/320.mp4", itemWith320pOnly.getBestPlaybackUrl())
    }

    @Test
    fun testVideoItemPosterImage() {
        val itemWithLandscape = VideoItem(
            landscape = "https://example.com/landscape.png",
            thumbnail = "https://example.com/thumb.png"
        )
        assertEquals("https://example.com/landscape.png", itemWithLandscape.getPosterImage())

        val itemWithThumbnailOnly = VideoItem(
            landscape = "",
            thumbnail = "https://example.com/thumb.png"
        )
        assertEquals("https://example.com/thumb.png", itemWithThumbnailOnly.getPosterImage())
    }

    @Test
    fun testVideoItemQualityMap() {
        val videoItem = VideoItem(
            video1080 = "https://example.com/1080.mp4",
            video320 = "https://example.com/320.mp4"
        )
        val qualityMap = videoItem.getQualityMap()
        assertEquals(2, qualityMap.size)
        assertEquals("https://example.com/1080.mp4", qualityMap["1080p (HD)"])
        assertEquals("https://example.com/320.mp4", qualityMap["320p (Low)"])
    }

    @Test
    fun testSectionResponseJsonParsing() {
        val sampleJson = """
            {
                "status": 200,
                "message": "Data Retrieved Successfully.",
                "result": [
                    {
                        "id": 29,
                        "title": "ठेठ राजस्थानी",
                        "screen_layout": "landscape",
                        "data": [
                            {
                                "id": 185,
                                "name": "EP_90.2_PROG-025246-SEG2",
                                "video_1080": "https://example.com/play_1080p.mp4",
                                "thumbnail": "https://example.com/thumb.png"
                            }
                        ]
                    }
                ]
            }
        """.trimIndent()

        val response = Gson().fromJson(sampleJson, SectionResponse::class.java)
        assertNotNull(response)
        assertEquals(200, response.status)
        assertEquals(1, response.result?.size)
        assertEquals("ठेठ राजस्थानी", response.result?.get(0)?.title)
        assertEquals("EP_90.2_PROG-025246-SEG2", response.result?.get(0)?.data?.get(0)?.name)
    }
}
