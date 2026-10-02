package com.example.videoplayerassignment.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.videoplayerassignment.data.model.VideoItem
import com.example.videoplayerassignment.databinding.ActivityMainBinding
import com.example.videoplayerassignment.ui.adapters.HeroBannerAdapter
import com.example.videoplayerassignment.ui.adapters.SectionAdapter
import com.example.videoplayerassignment.ui.viewmodel.HomeUiState
import com.example.videoplayerassignment.ui.viewmodel.MainViewModel
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()

    private lateinit var heroBannerAdapter: HeroBannerAdapter
    private lateinit var sectionAdapter: SectionAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupUI()
        observeViewModel()
    }

    private fun setupUI() {
        heroBannerAdapter = HeroBannerAdapter { videoItem ->
            openPlayer(videoItem)
        }
        binding.viewPagerHero.adapter = heroBannerAdapter

        sectionAdapter = SectionAdapter { selectedVideo, sectionVideos ->
            openPlayer(selectedVideo, ArrayList(sectionVideos))
        }

        binding.rvSections.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = sectionAdapter
        }

        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.loadSectionList()
        }

        binding.btnRetry.setOnClickListener {
            viewModel.loadSectionList()
        }

        binding.ivRefresh.setOnClickListener {
            binding.swipeRefreshLayout.isRefreshing = true
            viewModel.loadSectionList()
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    binding.swipeRefreshLayout.isRefreshing = false
                    when (state) {
                        is HomeUiState.Loading -> {
                            binding.progressBar.visibility = View.VISIBLE
                            binding.layoutError.visibility = View.GONE
                        }
                        is HomeUiState.Success -> {
                            binding.progressBar.visibility = View.GONE
                            binding.layoutError.visibility = View.GONE

                            if (state.heroVideos.isNotEmpty()) {
                                binding.layoutHeroContainer.visibility = View.VISIBLE
                                heroBannerAdapter.submitList(state.heroVideos)
                            } else {
                                binding.layoutHeroContainer.visibility = View.GONE
                            }

                            sectionAdapter.submitList(state.sections)
                        }
                        is HomeUiState.Error -> {
                            binding.progressBar.visibility = View.GONE
                            binding.layoutError.visibility = View.VISIBLE
                            binding.tvErrorMessage.text = state.message
                            Toast.makeText(this@MainActivity, state.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }

    private fun openPlayer(videoItem: VideoItem, playlist: ArrayList<VideoItem> = arrayListOf()) {
        val intent = Intent(this, PlayerActivity::class.java).apply {
            putExtra(PlayerActivity.EXTRA_VIDEO_ITEM, videoItem)
            putExtra(PlayerActivity.EXTRA_PLAYLIST, playlist)
        }
        startActivity(intent)
    }
}
