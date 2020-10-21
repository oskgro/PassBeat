package com.oskgro.passbeat.viewModel

import androidx.lifecycle.ViewModel

class ResultViewModel: ViewModel() {

    val videos = listOf("u3ltZmI5LQw", "SGqzOIal-9U", "CZkjeJKBI0M")

    fun randomVideoId(): String {
        val time = System.currentTimeMillis()
        val random = time % videos.size
        return videos.get(random.toInt())
    }
}