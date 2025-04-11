package dev.mayankmkh.kmpsample.videofeed.domain

data class VideoFeed(
    val posts: List<Post>,
)

data class Post(
    val canisterId: String,
    val postId: Int,
    val video: Video,
    val nsfwProbability: Float
)

data class Video(
    val id: String
) {
    val url = "https://customer-2p3jflss4r4hmpnz.cloudflarestream.com/$id/manifest/video.m3u8"
}
