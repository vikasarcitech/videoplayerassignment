package com.example.videoplayerassignment.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.videoplayerassignment.data.model.SectionItem
import com.example.videoplayerassignment.data.model.VideoItem
import com.example.videoplayerassignment.databinding.ItemSectionBinding

class SectionAdapter(
    private val onVideoClick: (VideoItem, List<VideoItem>) -> Unit
) : ListAdapter<SectionItem, SectionAdapter.SectionViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SectionViewHolder {
        val binding = ItemSectionBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return SectionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SectionViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class SectionViewHolder(
        private val binding: ItemSectionBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(section: SectionItem) {
            binding.tvSectionTitle.text = section.title ?: "Section"
            
            if (!section.description.isNullOrBlank()) {
                binding.tvSectionDescription.visibility = View.VISIBLE
                binding.tvSectionDescription.text = section.description
            } else {
                binding.tvSectionDescription.visibility = View.GONE
            }

            val videoAdapter = VideoAdapter(
                screenLayout = section.screenLayout
            ) { videoItem ->
                onVideoClick(videoItem, section.data)
            }

            binding.rvSectionContent.apply {
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                adapter = videoAdapter
                setHasFixedSize(true)
            }

            videoAdapter.submitList(section.data)
        }
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<SectionItem>() {
            override fun areItemsTheSame(oldItem: SectionItem, newItem: SectionItem): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: SectionItem, newItem: SectionItem): Boolean {
                return oldItem == newItem
            }
        }
    }
}
