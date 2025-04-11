package dev.mayankmkh.kmpsample.videofeed.ui

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.compose.PlayerSurface
import androidx.media3.ui.compose.SURFACE_TYPE_SURFACE_VIEW
import androidx.media3.ui.compose.state.rememberPresentationState
import dev.mayankmkh.kmpsample.videofeed.domain.VideoFeed
import dev.mayankmkh.kmpsample.videofeed.player.PlayerInteractor
import dev.mayankmkh.kmpsample.videofeed.presentation.UiState

@Composable
fun VideoFeedScreen(
    uiState: UiState<VideoFeed>,
    playerInteractor: PlayerInteractor,
    modifier: Modifier = Modifier,
) {
    Scaffold(modifier) {
        Box(
            modifier = Modifier
                .padding(it)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            when (uiState) {
                UiState.Initial -> Initial()
                UiState.InProgress -> InProgress()
                is UiState.Success -> Success(uiState.data, playerInteractor)
                is UiState.Failure -> Failure(uiState.throwable)
            }
        }
    }
}

@Composable
private fun Initial(modifier: Modifier = Modifier) {

}

@Composable
private fun InProgress(modifier: Modifier = Modifier) {
    CircularProgressIndicator()
}

@Composable
private fun Success(
    videofeed: VideoFeed,
    playerInteractor: PlayerInteractor,
    modifier: Modifier = Modifier,
) {
    val posts = videofeed.posts
    val videos = posts.map { it.video }
    playerInteractor.setVideos(videos)
    val pagerState = rememberPagerState(pageCount = { posts.size })

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect { page ->
            playerInteractor.onPageSelected(page)
        }
    }

    VerticalPager(
        state = pagerState,
        modifier = modifier.fillMaxSize(),
        beyondViewportPageCount = 1
    ) { page ->
        MediaPlayerPage(playerInteractor = playerInteractor, page = page)
    }
}

@Composable
fun MediaPlayerPage(
    modifier: Modifier = Modifier,
    playerInteractor: PlayerInteractor,
    page: Int
) {
    var player: Player? by remember(page) { mutableStateOf(null) }

    DisposableEffect(Unit) {
        Log.d("VideoFeedScreen", "MediaPlayerView: Acquiring player for page $page")
        playerInteractor.acquirePlayer(
            position = page,
            onPlayerAcquired = { player = it }
        )
        onDispose {
            Log.d("VideoFeedScreen", "MediaPlayerView: Releasing player for page $page")
            playerInteractor.releasePlayer(position = page)
            player = null
        }
    }
    Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        val mediaPlayer = player
        if (mediaPlayer == null) {
            Column {
                CircularProgressIndicator()
                Text("Loading...")
            }
        } else {
            MediaPlayerSurface(mediaPlayer, Modifier.fillMaxSize())
        }
    }
}

@androidx.annotation.OptIn(UnstableApi::class)
@Composable
private fun MediaPlayerSurface(player: Player, modifier: Modifier = Modifier) {

    val presentationState = rememberPresentationState(player)

    // Only use MediaPlayerScreen's modifier once for the top level Composable
    Box(modifier) {
        // Always leave PlayerSurface to be part of the Compose tree because it will be initialised in
        // the process. If this composable is guarded by some condition, it might never become visible
        // because the Player will not emit the relevant event, e.g. the first frame being ready.
        PlayerSurface(
            player = player,
            surfaceType = SURFACE_TYPE_SURFACE_VIEW,
        )

        if (presentationState.coverSurface) {
            // Cover the surface that is being prepared with a shutter
            // Do not use scaledModifier here, makes the Box be measured at 0x0
            Box(Modifier
                .matchParentSize()
                .background(Color.Black))
        }
    }
}

@Composable
private fun Failure(
    throwable: Throwable,
    modifier: Modifier = Modifier,
) {
    Text("Something went wrong")
}
