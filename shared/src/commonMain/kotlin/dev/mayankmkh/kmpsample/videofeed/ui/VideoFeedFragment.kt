package dev.mayankmkh.kmpsample.videofeed.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dev.mayankmkh.kmpsample.videofeed.domain.GetVideoFeedUseCase
import dev.mayankmkh.kmpsample.videofeed.presentation.VideoFeedViewModel

class VideoFeedFragment: Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {

            }
        }
    }
}

class VideoFeedViewModelFactory: ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return VideoFeedViewModel(GetVideoFeedUseCase()) as T
    }
}