package com.example.videoplayerassignment.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.videoplayerassignment.R
import com.example.videoplayerassignment.data.model.VideoItem
import com.example.videoplayerassignment.databinding.ItemVideoCardBigBinding
import com.example.videoplayerassignment.databinding.ItemVideoCardBinding
import com.example.videoplayerassignment.databinding.ItemVideoCardPortraitBinding

class VideoAdapter(
    private val screenLayout: String?,
    private val onVideoClick: (VideoItem) -> Unit
) : ListAdapter<VideoItem, RecyclerView.ViewHolder>(DiffCallback) {

    companion object {
        private const val VIEW_TYPE_LANDSCAPE = 1
        private const val VIEW_TYPE_BIG_LANDSCAPE = 2
        private const val VIEW_TYPE_PORTRAIT = 3

        private val DiffCallback = object : DiffUtil.ItemCallback<VideoItem>() {
            override fun areItemsTheSame(oldItem: VideoItem, newItem: VideoItem): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: VideoItem, newItem: VideoItem): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        val item = getItem(position)
        val layout = item.screenLayout ?: screenLayout
        return when {
            layout.equals("big_landscape", ignoreCase = true) -> VIEW_TYPE_BIG_LANDSCAPE
            layout.equals("portrait", ignoreCase = true) || layout.equals("shorts", ignoreCase = true) -> VIEW_TYPE_PORTRAIT
            else -> VIEW_TYPE_LANDSCAPE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEW_TYPE_BIG_LANDSCAPE -> {
                val binding = ItemVideoCardBigBinding.inflate(inflater, parent, false)
                BigLandscapeViewHolder(binding)
            }
            VIEW_TYPE_PORTRAIT -> {
                val binding = ItemVideoCardPortraitBinding.inflate(inflater, parent, false)
                PortraitViewHolder(binding)
            }
            else -> {
                val binding = ItemVideoCardBinding.inflate(inflater, parent, false)
                LandscapeViewHolder(binding)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        when (holder) {
            is LandscapeViewHolder -> holder.bind(item)
            is BigLandscapeViewHolder -> holder.bind(item)
            is PortraitViewHolder -> holder.bind(item)
        }
    }

    inner class LandscapeViewHolder(
        private val binding: ItemVideoCardBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: VideoItem) {
            binding.tvTitle.text = item.name ?: "Untitled Video"
            binding.tvViews.text = "${item.totalView} views"

            val image = item.getPosterImage()
            if (image.isNotEmpty()) {
                Glide.with(binding.ivThumbnail.context)
                    .load(image)
                    .placeholder(R.color.card_surface)
                    .error(R.color.card_surface)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(binding.ivThumbnail)
            }

            binding.root.setOnClickListener { onVideoClick(item) }
        }
    }

    inner class BigLandscapeViewHolder(
        private val binding: ItemVideoCardBigBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: VideoItem) {
            binding.tvTitle.text = item.name ?: "Untitled Video"
            binding.tvViews.text = "${item.totalView} views"

            val image = item.getPosterImage()
            if (image.isNotEmpty()) {
                Glide.with(binding.ivThumbnail.context)
                    .load(image)
                    .placeholder(R.color.card_surface)
                    .error(R.color.card_surface)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(binding.ivThumbnail)
            }

            binding.root.setOnClickListener { onVideoClick(item) }
        }
    }

    inner class PortraitViewHolder(
        private val binding: ItemVideoCardPortraitBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: VideoItem) {
            binding.tvTitle.text = item.name ?: "Untitled Short"

            val image = item.getPosterImage()
            if (image.isNotEmpty()) {
                Glide.with(binding.ivThumbnail.context)
                    .load(image)
                    .placeholder(R.color.card_surface)
                    .error(R.color.card_surface)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(binding.ivThumbnail)
            }

            binding.root.setOnClickListener { onVideoClick(item) }
        }
    }
}
