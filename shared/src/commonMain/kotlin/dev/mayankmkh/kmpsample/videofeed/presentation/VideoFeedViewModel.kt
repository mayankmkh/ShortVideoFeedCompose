package dev.mayankmkh.kmpsample.videofeed.presentation

import androidx.compose.ui.input.key.Key.Companion.U
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.mayankmkh.kmpsample.videofeed.domain.GetVideoFeedUseCase
import dev.mayankmkh.kmpsample.videofeed.domain.VideoFeed
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface UiState<out T> {
    data object Initial : UiState<Nothing>
    data object InProgress : UiState<Nothing>
    data class Success<T>(val data: T) : UiState<T>
    data class Failure(val throwable: Throwable) : UiState<Nothing>
}

public class VideoFeedViewModel internal constructor(
    private val getVideoFeed: GetVideoFeedUseCase,
) : ViewModel() {
    private val _uiStateFlow = MutableStateFlow<UiState<VideoFeed>>(UiState.Initial)
    val uiStateFlow: StateFlow<UiState<VideoFeed>> =
        _uiStateFlow

    private val eventChannel = Channel<Event>(Channel.BUFFERED)
    internal val eventsFlow: Flow<Event> = eventChannel.receiveAsFlow()

    init {
        loadData()
    }

    private fun loadData() {
        _uiStateFlow.value = UiState.InProgress
        viewModelScope.launch {
            val result = getVideoFeed()
            val newState = result.fold(
                onSuccess = { UiState.Success(it) },
                onFailure = { UiState.Failure(it) }
            )
            _uiStateFlow.value = newState
        }
    }

    private fun sendToChannel(event: Event) {
        viewModelScope.launch { eventChannel.send(event) }
    }

    internal sealed class Event {
        internal data object SomethingHappened : Event()

        internal data class SomethingHappenedWithData(val data: Nothing) : Event()
    }
}
