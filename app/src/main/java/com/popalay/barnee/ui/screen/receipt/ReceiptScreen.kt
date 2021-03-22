package com.popalay.barnee.ui.screen.receipt

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.airbnb.mvrx.compose.collectAsState
import com.airbnb.mvrx.compose.mavericksViewModel
import com.popalay.barnee.ui.common.YouTubePlayer
import com.popalay.barnee.ui.theme.BarneeTheme
import dev.chrisbanes.accompanist.coil.CoilImage
import dev.chrisbanes.accompanist.insets.navigationBarsPadding

@Composable
fun ReceiptScreen(
    steps: List<String>,
    image: String,
    video: String
) {
    val viewModel: ReceiptViewModel = mavericksViewModel()
    val state by viewModel.collectAsState()
    Scaffold {
        val scrollState = rememberScrollState()
        Column(
            modifier = Modifier
                .navigationBarsPadding()
                .verticalScroll(scrollState)
        ) {
            Box {
                if (state.isPlaying) {
                    YouTubePlayer(
                        uri = video,
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1F)
                    )
                } else {
                    CoilImage(
                        data = image,
                        contentScale = ContentScale.Crop,
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1F)
                    )
                }
                if (video.isNotBlank() && !state.isPlaying) {
                    IconButton(
                        onClick = { viewModel.togglePlaying() },
                        modifier = Modifier
                            .padding(24.dp)
                            .clip(CircleShape)
                            .align(Alignment.Center)
                            .background(Color.Black.copy(alpha = 0.3F))
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            tint = Color.White,
                            contentDescription = "Play",
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            }
            Text(
                text = "Steps",
                style = MaterialTheme.typography.h2,
                modifier = Modifier
                    .padding(horizontal = 32.dp)
                    .padding(top = 32.dp)
            )
            Column(
                modifier = Modifier
                    .padding(horizontal = 32.dp)
                    .padding(top = 16.dp)
            ) {
                steps.forEachIndexed { index, step ->
                    Row {
                        Text(
                            text = index.toString().padStart(2, '0'),
                            color = MaterialTheme.colors.primary,
                            style = MaterialTheme.typography.h3,
                            modifier = Modifier.defaultMinSize(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = step,
                            style = MaterialTheme.typography.body1
                        )
                    }
                    if (index != steps.lastIndex) {
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}

@Preview("Light Theme", widthDp = 360, heightDp = 640)
@Composable
fun ReceiptScreenLightPreview() {
    BarneeTheme {
        ReceiptScreen(emptyList(), "", "")
    }
}

@Preview("Dark Theme", widthDp = 360, heightDp = 640)
@Composable
fun ReceiptScreenDarkPreview() {
    BarneeTheme(darkTheme = true) {
        ReceiptScreen(emptyList(), "", "")
    }
}