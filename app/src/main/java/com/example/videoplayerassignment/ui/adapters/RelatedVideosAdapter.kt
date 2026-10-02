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
import com.example.videoplayerassignment.databinding.ItemRelatedVideoBinding

class RelatedVideosAdapter(
    private val onVideoClick: (VideoItem) -> Unit
) : ListAdapter<VideoItem, RelatedVideosAdapter.RelatedViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RelatedViewHolder {
        val binding = ItemRelatedVideoBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return RelatedViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RelatedViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class RelatedViewHolder(
        private val binding: ItemRelatedVideoBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: VideoItem) {
            binding.tvRelatedTitle.text = item.name ?: "Untitled Video"
            binding.tvRelatedCategory.text = item.categoryName ?: "${item.totalView} views"

            val poster = item.getPosterImage()
            if (poster.isNotEmpty()) {
                Glide.with(binding.ivRelatedThumbnail.context)
                    .load(poster)
                    .placeholder(R.color.card_surface)
                    .error(R.color.card_surface)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(binding.ivRelatedThumbnail)
            }

            binding.root.setOnClickListener {
                onVideoClick(item)
            }
        }
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<VideoItem>() {
            override fun areItemsTheSame(oldItem: VideoItem, newItem: VideoItem): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: VideoItem, newItem: VideoItem): Boolean {
                return oldItem == newItem
            }
        }
    }
}
