package dev.mayankmkh.kmpsample.videofeed.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json

private const val URL = "https://yral-ml-feed-server.fly.dev/api/v1/feed/coldstart/clean"

internal class VideoFeedRemoteDataSource {
    private val client = HttpClient(OkHttp) {
        install(ContentNegotiation) {
            json()
        }
    }

    suspend fun getVideoFeed(): VideoFeedDto = client.post(URL) {
        setBody(
            VideoRequestDto(
                canisterId = "76qol-iiaaa-aaaak-qelkq-cai",
                filterResults = emptyList(),
                numResults = 10
            )
        )
        contentType(ContentType.Application.Json)
    }.body()
}

