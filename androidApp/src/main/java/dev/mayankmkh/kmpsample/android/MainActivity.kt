package dev.mayankmkh.kmpsample.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import dev.mayankmkh.kmpsample.videofeed.presentation.VideoFeedViewModel
import dev.mayankmkh.kmpsample.videofeed.player.PlayerInteractor
import dev.mayankmkh.kmpsample.videofeed.ui.VideoFeedScreen
import dev.mayankmkh.kmpsample.videofeed.ui.VideoFeedViewModelFactory

class MainActivity : ComponentActivity() {
    private val viewModel by viewModels<VideoFeedViewModel> { VideoFeedViewModelFactory() }
    private lateinit var playerInteractor: PlayerInteractor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        playerInteractor = PlayerInteractor(context = this, numberOfPlayers = 4)
        setContent {
            MyApplicationTheme {
                val uiState by viewModel.uiStateFlow.collectAsState()
                VideoFeedScreen(uiState, playerInteractor)
            }
        }
    }

    override fun onDestroy() {
        playerInteractor.release()
        super.onDestroy()
    }
}

@Composable
fun GreetingView(text: String) {
    Text(text = text)
}

@Preview
@Composable
fun DefaultPreview() {
    MyApplicationTheme {
        GreetingView("Hello, Android!")
    }
}
