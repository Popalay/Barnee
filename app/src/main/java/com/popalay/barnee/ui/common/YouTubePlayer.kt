package com.popalay.barnee.ui.common

import android.util.SparseArray
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import at.huber.youtubeExtractor.VideoMeta
import at.huber.youtubeExtractor.YouTubeExtractor
import at.huber.youtubeExtractor.YtFile
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource

@Composable
fun YouTubePlayer(uri: String, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val lifecycle = LocalLifecycleOwner.current.lifecycle

    var autoPlay by rememberSaveable { mutableStateOf(true) }
    var window by rememberSaveable { mutableStateOf(0) }
    var position by rememberSaveable { mutableStateOf(0L) }

    val player = remember(uri) {
        SimpleExoPlayer.Builder(context).build().apply {
            val defaultHttpDataSourceFactory = DefaultHttpDataSource.Factory()
                .setUserAgent("barnee")
            object : YouTubeExtractor(context) {
                override fun onExtractionComplete(ytFiles: SparseArray<YtFile>?, videoMeta: VideoMeta?) {
                    val videoUrl = ytFiles?.get(22)?.url.orEmpty()
                    setMediaSource(
                        ProgressiveMediaSource.Factory(defaultHttpDataSourceFactory)
                            .createMediaSource(MediaItem.fromUri(videoUrl))
                    )
                }
            }.extract(uri, true, true)
            playWhenReady = autoPlay
            seekTo(window, position)
        }
    }

    fun updateState() {
        autoPlay = player.playWhenReady
        window = player.currentWindowIndex
        position = 0L.coerceAtLeast(player.contentPosition)
    }

    val playerView = remember {
        val playerView = PlayerView(context)
        lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onResume(owner: LifecycleOwner) {
                playerView.onResume()
                player.playWhenReady = autoPlay
            }

            override fun onPause(owner: LifecycleOwner) {
                updateState()
                playerView.onPause()
                player.playWhenReady = false
            }
        })
        playerView
    }

    DisposableEffect(Unit) {
        onDispose {
            updateState()
            player.release()
        }
    }

    AndroidView(
        factory = { playerView },
        modifier = modifier
    ) {
        playerView.player = player
        player.play()
    }
}