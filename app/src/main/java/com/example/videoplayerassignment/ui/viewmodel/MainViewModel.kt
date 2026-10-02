package com.example.videoplayerassignment.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.videoplayerassignment.data.model.SectionItem
import com.example.videoplayerassignment.data.model.VideoItem
import com.example.videoplayerassignment.data.repository.Result
import com.example.videoplayerassignment.data.repository.VideoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class HomeUiState {
    object Loading : HomeUiState()
    data class Success(
        val heroVideos: List<VideoItem>,
        val sections: List<SectionItem>
    ) : HomeUiState()
    data class Error(val message: String) : HomeUiState()
}

class MainViewModel(
    private val repository: VideoRepository = VideoRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadSectionList()
    }

    fun loadSectionList() {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading
            when (val result = repository.getSectionList()) {
                is Result.Success -> {
                    val allSections = result.data.filter { it.data.isNotEmpty() }
                    
                    val featuredList = mutableListOf<VideoItem>()
                    allSections.forEach { section ->
                        featuredList.addAll(section.data.take(2))
                    }
                    val heroVideos = featuredList.distinctBy { it.id }.take(5)

                    _uiState.value = HomeUiState.Success(
                        heroVideos = heroVideos,
                        sections = allSections
                    )
                }
                is Result.Error -> {
                    _uiState.value = HomeUiState.Error(result.message)
                }
                is Result.Loading -> {
                    _uiState.value = HomeUiState.Loading
                }
            }
        }
    }
}
