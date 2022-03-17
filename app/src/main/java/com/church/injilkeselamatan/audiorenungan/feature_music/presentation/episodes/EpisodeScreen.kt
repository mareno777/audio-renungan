package com.church.injilkeselamatan.audiorenungan.feature_music.presentation.episodes

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.church.injilkeselamatan.audiorenungan.di.Constants
import com.church.injilkeselamatan.audiorenungan.feature_music.presentation.albums.components.PlayingNowSection
import com.church.injilkeselamatan.audiorenungan.feature_music.presentation.episodes.components.EpisodeItem
import com.church.injilkeselamatan.audiorenungan.feature_music.presentation.util.Screen
import com.church.injilkeselamatan.audiorenungan.feature_music.ui.productSansGoogle
import com.church.injilkeselamatan.core.util.extensions.id

@Composable
fun EpisodeScreen(
    navController: NavController,
    viewModel: EpisodeViewModel = hiltViewModel()
) {
    val state by viewModel.state
    val downloadedState by viewModel.downloadState
    var isExpanded by remember {
        mutableStateOf(false)
    }
    val maxProgress by viewModel.totalBytesToDownload.collectAsState(1f)
    val downloadedLength by viewModel.bytesDownloaded.collectAsState(0f)

    val description = when (viewModel.currentSelectedAlbum) {
        "Pohon Kehidupan" -> Constants.DESC_PK
        "Belajar Takut Akan Tuhan" -> Constants.DESC_BTAT
        "Saat Teduh Bersama Tuhan" -> Constants.DESC_STBT
        else -> ""
    }

    val onDownloadComplated = viewModel.onDownloadComplated().observeAsState()
    val mediaMetadata by viewModel.playingMetadata().collectAsState()
    val playbackState by viewModel.currentPlaybackstate().collectAsState()

    LaunchedEffect(onDownloadComplated) {
        viewModel.loadDownloadedEpisodes()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            item {
                Text(
                    text = viewModel.currentSelectedAlbum,
                    style = MaterialTheme.typography.h4,
                    fontFamily = productSansGoogle,
                    color = MaterialTheme.colors.onSurface,
                    modifier = Modifier.padding(top = 20.dp, start = 20.dp)
                )
                Spacer(modifier = Modifier.height(15.dp))
                Text(
                    text = description,
                    fontFamily = productSansGoogle,
                    style = MaterialTheme.typography.body1,
                    color = MaterialTheme.colors.onSurface,
                    maxLines = if (isExpanded) Int.MAX_VALUE else 4,
                    modifier = Modifier.padding(start = 20.dp, end = 20.dp)
                )
                Spacer(modifier = Modifier.padding(vertical = 8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(end = 20.dp)
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ) {
                            isExpanded = !isExpanded
                        },
                    horizontalArrangement = Arrangement.End
                ) {
                    Text(
                        text = if (isExpanded) "show less" else "show more",
                        color = Color(0xFF0073FF),
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.End
                    )
                    Icon(
                        if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        tint = Color(0xFF0073FF)
                    )
                }
                Spacer(modifier = Modifier.height(15.dp))
                Text(
                    text = "Episodes",
                    fontFamily = productSansGoogle,
                    style = MaterialTheme.typography.h6,
                    color = MaterialTheme.colors.onSurface,
                    modifier = Modifier.padding(start = 20.dp, end = 20.dp)
                )
                Spacer(modifier = Modifier.height(15.dp))
                Divider()
            }
            items(state.songs) { song ->

                EpisodeItem(
                    song = song,
                    totalBytesToDownload = maxProgress,
                    bytesDownloaded = downloadedLength,
                    downloaded = downloadedState.songs.any {
                        it.id == song.id
                    },
                    onPlayToggleClicked = {
                        viewModel.onEvent(EpisodesEvent.PlayToogle(song, true))
                    },
                    onDownloadClicked = {
                        viewModel.onEvent(EpisodesEvent.DownloadEpisode(song))
                    },
                    onRemoveDownloadClicked = {
                        viewModel.onEvent(EpisodesEvent.RemoveDownloadedEpisode(song))
                    },
                    state = viewModel.onState(song.id!!),
                    mediaMetadata = mediaMetadata,
                    playbackState = playbackState
                )
                Divider()
            }
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                )
            }
        }
        PlayingNowSection(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .clickable(
                    interactionSource = MutableInteractionSource(),
                    indication = null
                ) {
                    navController.navigate(Screen.PlayerScreen.route)
                },
            playbackStateCompat = playbackState,
            mediaMetadataCompat = mediaMetadata
        ) {
            viewModel.onEvent(EpisodesEvent.PlayOrPause(it))
        }
    }
}