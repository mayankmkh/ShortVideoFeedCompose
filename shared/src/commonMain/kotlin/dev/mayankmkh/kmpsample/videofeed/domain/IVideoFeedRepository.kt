package dev.mayankmkh.kmpsample.videofeed.domain

internal interface IVideoFeedRepository {
    suspend fun getVideoFeed(): VideoFeed
}
