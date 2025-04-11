package dev.mayankmkh.kmpsample.videofeed.domain

import dev.mayankmkh.kmpsample.videofeed.data.VideoFeedRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal class GetVideoFeedUseCase {

    private val repository: IVideoFeedRepository = VideoFeedRepository()

    suspend operator fun invoke(): Result<VideoFeed> = withContext(Dispatchers.IO) {
        runCatching {
            repository.getVideoFeed()
        }.onFailure {
            if (it is CancellationException) throw it
        }
    }
}
