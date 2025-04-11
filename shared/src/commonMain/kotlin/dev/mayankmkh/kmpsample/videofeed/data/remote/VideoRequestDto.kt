package dev.mayankmkh.kmpsample.videofeed.data.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VideoRequestDto(
    @SerialName("canister_id") private val canisterId: String,
    @SerialName("filter_results") private val filterResults: List<String> = emptyList(),
    @SerialName("num_results") private val numResults: Int
)
