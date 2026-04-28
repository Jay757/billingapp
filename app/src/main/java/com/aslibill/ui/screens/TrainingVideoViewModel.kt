package com.aslibill.ui.screens

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class TrainingVideo(
    val title: String,
    val duration: String,
    val videoUrl: String = "" // Placeholder for actual video link
)

class TrainingVideoViewModel : ViewModel() {
    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _videos = MutableStateFlow(
        listOf(
            TrainingVideo("Getting Started with BillSuper", "2:30"),
            TrainingVideo("How to Make a Quick Bill", "1:45"),
            TrainingVideo("Managing Your Inventory", "3:10"),
            TrainingVideo("Understanding Sales Reports", "4:20"),
            TrainingVideo("Setting Up Bluetooth Printer", "2:55"),
            TrainingVideo("Managing Customer Credits", "3:30"),
            TrainingVideo("Staff Access Control", "2:15")
        )
    )
    val videos: StateFlow<List<TrainingVideo>> = _videos.asStateFlow()

    init {
        // Simulate local data load completion
        _isLoading.value = false
    }

    fun playVideo(video: TrainingVideo) {
        // In a real app, this would open a video player or YouTube intent
    }
}
