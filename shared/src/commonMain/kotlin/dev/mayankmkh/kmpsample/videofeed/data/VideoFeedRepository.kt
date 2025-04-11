package dev.mayankmkh.kmpsample.videofeed.data

import dev.mayankmkh.kmpsample.videofeed.data.remote.VideoFeedRemoteDataSource
import dev.mayankmkh.kmpsample.videofeed.data.remote.toVideoFeed
import dev.mayankmkh.kmpsample.videofeed.domain.IVideoFeedRepository
import dev.mayankmkh.kmpsample.videofeed.domain.VideoFeed

internal class VideoFeedRepository(
) : IVideoFeedRepository {
    private val remoteDataSource: VideoFeedRemoteDataSource = VideoFeedRemoteDataSource()

    override suspend fun getVideoFeed(): VideoFeed {
        return remoteDataSource.getVideoFeed().toVideoFeed()
    }
}
