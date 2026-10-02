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
import com.example.videoplayerassignment.databinding.ItemHeroBannerBinding

class HeroBannerAdapter(
    private val onVideoClick: (VideoItem) -> Unit
) : ListAdapter<VideoItem, HeroBannerAdapter.HeroViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HeroViewHolder {
        val binding = ItemHeroBannerBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return HeroViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HeroViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class HeroViewHolder(
        private val binding: ItemHeroBannerBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: VideoItem) {
            binding.tvHeroTitle.text = item.name ?: "Featured Content"
            binding.tvHeroCategory.text = item.categoryName ?: "Gangaur Special"

            val posterUrl = item.getPosterImage()
            if (posterUrl.isNotEmpty()) {
                Glide.with(binding.ivHeroPoster.context)
                    .load(posterUrl)
                    .placeholder(R.color.card_surface)
                    .error(R.color.card_surface)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(binding.ivHeroPoster)
            }

            binding.btnHeroPlay.setOnClickListener {
                onVideoClick(item)
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
