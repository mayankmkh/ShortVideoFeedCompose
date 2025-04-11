package dev.mayankmkh.kmpsample.videofeed.data.remote

import dev.mayankmkh.kmpsample.videofeed.domain.Post
import dev.mayankmkh.kmpsample.videofeed.domain.Video
import dev.mayankmkh.kmpsample.videofeed.domain.VideoFeed
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class VideoFeedDto(
    val posts: List<PostDto>,
)

@Serializable
internal data class PostDto(
    @SerialName("canister_id") val canisterId: String,
    @SerialName("post_id") val postId: Int,
    @SerialName("video_id") val videoId: String,
    @SerialName("nsfw_probability") val nsfwProbability: Float
)

internal fun VideoFeedDto.toVideoFeed() = VideoFeed(
    posts = posts.map { post -> post.toPost() }
)

private fun PostDto.toPost() = Post(
    canisterId = canisterId,
    postId = postId,
    video = Video(videoId),
    nsfwProbability = nsfwProbability
)
