/*
 * Copyright (c) 2023 Denys Nykyforov
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.popalay.barnee.ui.common

import android.content.Context
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
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.google.android.exoplayer2.util.Util

@Composable
fun YouTubePlayer(uri: String, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val lifecycle = LocalLifecycleOwner.current.lifecycle

    var autoPlay by rememberSaveable { mutableStateOf(true) }
    var window by rememberSaveable { mutableStateOf(0) }
    var position by rememberSaveable { mutableStateOf(0L) }

    val exoPlayer = remember {
        ExoPlayer.Builder(context).build()
    }

    fun updateState() {
        autoPlay = exoPlayer.playWhenReady
        window = exoPlayer.currentMediaItemIndex
        position = 0L.coerceAtLeast(exoPlayer.contentPosition)
    }

    DisposableEffect(uri) {
        val defaultHttpDataSourceFactory = DefaultHttpDataSource.Factory()
            .setUserAgent(Util.getUserAgent(context, context.packageName))
        uri.extractYouTubeUrl(context) { videoUrl ->
            val source = ProgressiveMediaSource.Factory(defaultHttpDataSourceFactory)
                .createMediaSource(MediaItem.fromUri(videoUrl))
            exoPlayer.setMediaSource(source)
            exoPlayer.prepare()
        }
        exoPlayer.playWhenReady = autoPlay
        exoPlayer.seekTo(window, position)

        onDispose {
            updateState()
            exoPlayer.release()
        }
    }

    AndroidView(
        factory = {
            StyledPlayerView(context).also { playerView ->
                playerView.player = exoPlayer
                lifecycle.addObserver(object : DefaultLifecycleObserver {
                    override fun onResume(owner: LifecycleOwner) {
                        playerView.onResume()
                        exoPlayer.playWhenReady = autoPlay
                    }

                    override fun onPause(owner: LifecycleOwner) {
                        updateState()
                        playerView.onPause()
                        exoPlayer.playWhenReady = false
                    }
                })
            }
        },
        modifier = modifier
    )
}

private fun String.extractYouTubeUrl(context: Context, onResult: (String) -> Unit) {
    object : YouTubeExtractor(context) {
        override fun onExtractionComplete(ytFiles: SparseArray<YtFile>?, videoMeta: VideoMeta?) {
            val videoUrl = ytFiles?.get(22)?.url.orEmpty()
            onResult(videoUrl)
        }
    }.extract(this)
}
