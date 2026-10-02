package com.example.videoplayerassignment.ui

import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.videoplayerassignment.data.model.VideoItem
import com.example.videoplayerassignment.databinding.ActivityPlayerBinding
import com.example.videoplayerassignment.ui.adapters.RelatedVideosAdapter
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class PlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlayerBinding
    private var exoPlayer: ExoPlayer? = null

    private var currentVideoItem: VideoItem? = null
    private var playlist: ArrayList<VideoItem> = arrayListOf()

    private var currentPlaybackPosition: Long = 0L
    private var currentQualityLabel: String = "Auto"
    private var currentQualityUrl: String = ""

    private lateinit var relatedVideosAdapter: RelatedVideosAdapter

    companion object {
        const val EXTRA_VIDEO_ITEM = "extra_video_item"
        const val EXTRA_PLAYLIST = "extra_playlist"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        currentVideoItem = getSerializableExtra(EXTRA_VIDEO_ITEM, VideoItem::class.java)
        playlist = getSerializableListExtra(EXTRA_PLAYLIST, VideoItem::class.java) ?: arrayListOf()

        if (currentVideoItem == null) {
            Toast.makeText(this, "Video not available", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        setupUI()
        loadVideoDetails(currentVideoItem!!)
    }

    private fun setupUI() {
        binding.btnBack.setOnClickListener {
            finish()
        }

        relatedVideosAdapter = RelatedVideosAdapter { videoItem ->
            playNewVideo(videoItem)
        }

        binding.rvRelatedVideos.apply {
            layoutManager = LinearLayoutManager(this@PlayerActivity)
            adapter = relatedVideosAdapter
        }

        val relatedList = playlist.filter { it.id != currentVideoItem?.id }
        if (relatedList.isNotEmpty()) {
            binding.tvUpNextHeader.visibility = View.VISIBLE
            binding.rvRelatedVideos.visibility = View.VISIBLE
            relatedVideosAdapter.submitList(relatedList)
        } else {
            binding.tvUpNextHeader.visibility = View.GONE
            binding.rvRelatedVideos.visibility = View.GONE
        }

        binding.btnQuality.setOnClickListener {
            showQualitySelectionDialog()
        }
    }

    private fun loadVideoDetails(videoItem: VideoItem) {
        binding.tvVideoTitle.text = videoItem.name ?: "Untitled Video"
        binding.tvCategoryBadge.text = videoItem.categoryName ?: "Gangaur TV"
        binding.tvViewsCount.text = "${videoItem.totalView} views"
        binding.tvLikesCount.text = "${videoItem.totalLike} likes"

        if (!videoItem.description.isNullOrBlank()) {
            binding.tvDescription.visibility = View.VISIBLE
            binding.tvDescription.text = videoItem.description
        } else {
            binding.tvDescription.visibility = View.GONE
        }

        val qualityMap = videoItem.getQualityMap()
        if (qualityMap.isNotEmpty()) {
            binding.btnQuality.visibility = View.VISIBLE
            val firstEntry = qualityMap.entries.first()
            currentQualityLabel = firstEntry.key
            currentQualityUrl = firstEntry.value
            binding.btnQuality.text = currentQualityLabel
        } else {
            currentQualityUrl = videoItem.getBestPlaybackUrl()
            binding.btnQuality.visibility = View.GONE
        }
    }

    private fun initializePlayer() {
        if (currentQualityUrl.isBlank()) {
            Toast.makeText(this, "Video stream URL unavailable", Toast.LENGTH_SHORT).show()
            return
        }

        if (exoPlayer == null) {
            exoPlayer = ExoPlayer.Builder(this).build().apply {
                binding.playerView.player = this
                playWhenReady = true

                addListener(object : Player.Listener {
                    override fun onPlayerError(error: PlaybackException) {
                        Toast.makeText(
                            this@PlayerActivity,
                            "Playback error: ${error.localizedMessage}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                })
            }
        }

        val mediaItem = MediaItem.fromUri(currentQualityUrl)
        exoPlayer?.apply {
            setMediaItem(mediaItem, currentPlaybackPosition)
            prepare()
            play()
        }
    }

    private fun showQualitySelectionDialog() {
        val videoItem = currentVideoItem ?: return
        val qualityMap = videoItem.getQualityMap()
        if (qualityMap.isEmpty()) return

        val labels = qualityMap.keys.toTypedArray()
        val urls = qualityMap.values.toTypedArray()

        var selectedIndex = labels.indexOf(currentQualityLabel)
        if (selectedIndex == -1) selectedIndex = 0

        MaterialAlertDialogBuilder(this)
            .setTitle("Select Video Quality")
            .setSingleChoiceItems(labels, selectedIndex) { dialog, which ->
                val newLabel = labels[which]
                val newUrl = urls[which]

                if (newUrl != currentQualityUrl) {
                    currentPlaybackPosition = exoPlayer?.currentPosition ?: 0L
                    currentQualityLabel = newLabel
                    currentQualityUrl = newUrl
                    binding.btnQuality.text = newLabel

                    val mediaItem = MediaItem.fromUri(newUrl)
                    exoPlayer?.apply {
                        setMediaItem(mediaItem, currentPlaybackPosition)
                        prepare()
                        play()
                    }
                }
                dialog.dismiss()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun playNewVideo(videoItem: VideoItem) {
        currentVideoItem = videoItem
        currentPlaybackPosition = 0L

        loadVideoDetails(videoItem)

        val relatedList = playlist.filter { it.id != videoItem.id }
        relatedVideosAdapter.submitList(relatedList)

        if (currentQualityUrl.isNotBlank()) {
            val mediaItem = MediaItem.fromUri(currentQualityUrl)
            exoPlayer?.apply {
                setMediaItem(mediaItem, 0L)
                prepare()
                play()
            }
        }
    }

    private fun releasePlayer() {
        exoPlayer?.let { player ->
            currentPlaybackPosition = player.currentPosition
            player.release()
        }
        exoPlayer = null
    }

    override fun onStart() {
        super.onStart()
        if (Build.VERSION.SDK_INT > 23) {
            initializePlayer()
        }
    }

    override fun onResume() {
        super.onResume()
        if (Build.VERSION.SDK_INT <= 23 || exoPlayer == null) {
            initializePlayer()
        }
    }

    override fun onPause() {
        super.onPause()
        if (Build.VERSION.SDK_INT <= 23) {
            releasePlayer()
        }
    }

    override fun onStop() {
        super.onStop()
        if (Build.VERSION.SDK_INT > 23) {
            releasePlayer()
        }
    }

    @Suppress("DEPRECATION", "UNCHECKED_CAST")
    private fun <T : java.io.Serializable> getSerializableExtra(key: String, clazz: Class<T>): T? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra(key, clazz)
        } else {
            intent.getSerializableExtra(key) as? T
        }
    }

    @Suppress("DEPRECATION", "UNCHECKED_CAST")
    private fun <T : java.io.Serializable> getSerializableListExtra(key: String, clazz: Class<T>): ArrayList<T>? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra(key, ArrayList::class.java) as? ArrayList<T>
        } else {
            intent.getSerializableExtra(key) as? ArrayList<T>
        }
    }
}
