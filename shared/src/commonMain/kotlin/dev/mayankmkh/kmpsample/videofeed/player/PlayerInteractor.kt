package dev.mayankmkh.kmpsample.videofeed.player

import android.content.Context
import android.util.Log
import androidx.annotation.OptIn
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.MediaSource
import androidx.media3.exoplayer.source.preload.DefaultPreloadManager
import androidx.media3.exoplayer.source.preload.DefaultPreloadManager.Status.STAGE_LOADED_FOR_DURATION_MS
import androidx.media3.exoplayer.source.preload.TargetPreloadStatusControl
import dev.mayankmkh.kmpsample.videofeed.domain.Video
import kotlin.math.abs

/**
 * Inspired from [ViewPagerMediaAdapter](https://github.com/androidx/media/blob/51efcad6720d3016d4b17fe8a1e9a11ca89e8b67/demos/shortform/src/main/java/androidx/media3/demo/shortform/viewpager/ViewPagerMediaAdapter.kt)
 */
@OptIn(UnstableApi::class)
class PlayerInteractor(
    context: Context,
    numberOfPlayers: Int
) {
    private val preloadManager: DefaultPreloadManager
    private val playerPool: PlayerPool
    private val preloadControl: DefaultPreloadControl
    private var mediaItems: List<MediaItem> = emptyList()

    private val positionToPlayerMap = mutableMapOf<Int, Player>()

    companion object {
        private const val TAG = "PlayerInteractor"
        private const val LOAD_CONTROL_MIN_BUFFER_MS = 5_000
        private const val LOAD_CONTROL_MAX_BUFFER_MS = 20_000
        private const val LOAD_CONTROL_BUFFER_FOR_PLAYBACK_MS = 500
    }

    init {
        Log.d(TAG, "init")
        val loadControl =
            DefaultLoadControl.Builder()
                .setBufferDurationsMs(
                    LOAD_CONTROL_MIN_BUFFER_MS,
                    LOAD_CONTROL_MAX_BUFFER_MS,
                    LOAD_CONTROL_BUFFER_FOR_PLAYBACK_MS,
                    DefaultLoadControl.DEFAULT_BUFFER_FOR_PLAYBACK_AFTER_REBUFFER_MS,
                )
                .setPrioritizeTimeOverSizeThresholds(true)
                .build()
        preloadControl = DefaultPreloadControl()
        val preloadManagerBuilder =
            DefaultPreloadManager.Builder(context.applicationContext, preloadControl)
                .setLoadControl(loadControl)
        playerPool = PlayerPool(numberOfPlayers, preloadManagerBuilder)
        preloadManager = preloadManagerBuilder.build()
    }

    fun setVideos(videos: List<Video>) {
        Log.d(TAG, "setVideos ${videos.size}")
        val mediaItems = videos.mapIndexed { index, video ->
            MediaItem.Builder().setUri(video.url).setMediaId(index.toString()).build()
        }
        setMediaItems(mediaItems)
    }

    private fun setMediaItems(mediaItems: List<MediaItem>) {
        Log.d(TAG, "setMediaItems ${mediaItems.size}")
        if (this.mediaItems == mediaItems) return
        Log.d(TAG, "setMediaItems: resetting")
        this.mediaItems = mediaItems
        preloadManager.reset()
        mediaItems.forEachIndexed { index, mediaItem ->
            preloadManager.add(mediaItem, index)
        }
        preloadManager.invalidate()
    }

    fun release() {
        Log.d(TAG, "release")
        positionToPlayerMap.clear()
        playerPool.destroyPlayers()
        preloadManager.release()
    }

    private fun getMediaSourceAt(position: Int): MediaSource {
        Log.d(TAG, "getMediaSourceAt: Getting item at position $position")
        val mediaItem = mediaItems[position]
        return checkNotNull(preloadManager.getMediaSource(mediaItem))
    }

    fun onPageSelected(position: Int) {
        val player = positionToPlayerMap[position]
        Log.d(TAG, "onPageSelected: Playing item at position $position ${player != null}")
        player?.let { playerPool.play(it) }
        preloadControl.currentPlayingIndex = position
        preloadManager.setCurrentPlayingIndex(position)
        preloadManager.invalidate()
    }

    fun acquirePlayer(position: Int, onPlayerAcquired: (ExoPlayer) -> Unit) {
        Log.d(TAG, "acquirePlayer: Getting item at position $position")
        playerPool.acquirePlayer(position, callback = {
            setupPlayer(it, getMediaSourceAt(position))
            positionToPlayerMap[position] = it
            onPlayerAcquired(it)
            if (preloadControl.currentPlayingIndex == position) {
                playerPool.play(it)
            }
        })
    }

    fun releasePlayer(position: Int) {
        Log.d(TAG, "releasePlayer: Getting item at position $position")
        val removedPlayer = positionToPlayerMap.remove(position)
        playerPool.releasePlayer(position, removedPlayer)
    }

    private fun setupPlayer(player: ExoPlayer, mediaSource: MediaSource) {
        Log.d(TAG, "setupPlayer: mediaSource ${mediaSource.mediaItem.mediaId}")
        player.run {
            repeatMode = ExoPlayer.REPEAT_MODE_ONE
            setMediaSource(mediaSource)
            seekTo(currentPosition)
            player.prepare()
        }
    }

    inner class DefaultPreloadControl(var currentPlayingIndex: Int = C.INDEX_UNSET) :
        TargetPreloadStatusControl<Int> {

        override fun getTargetPreloadStatus(rankingData: Int): DefaultPreloadManager.Status? {
            if (abs(rankingData - currentPlayingIndex) == 2) {
                return DefaultPreloadManager.Status(STAGE_LOADED_FOR_DURATION_MS, 500L)
            } else if (abs(rankingData - currentPlayingIndex) == 1) {
                return DefaultPreloadManager.Status(STAGE_LOADED_FOR_DURATION_MS, 1000L)
            }
            return null
        }
    }
}
