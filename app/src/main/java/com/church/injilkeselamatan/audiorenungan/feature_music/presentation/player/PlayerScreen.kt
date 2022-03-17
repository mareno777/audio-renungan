package com.church.injilkeselamatan.audiorenungan.feature_music.presentation.player

import androidx.compose.foundation.layout.*
import androidx.compose.material.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.church.injilkeselamatan.core.util.extensions.id
import com.church.injilkeselamatan.audiorenungan.feature_music.presentation.player.components.*
import com.church.injilkeselamatan.core.NOTHING_PLAYING
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.rememberPagerState

@ExperimentalPagerApi
@Composable
fun PlayerScreen(navController: NavController, viewModel: PlayerViewModel = hiltViewModel()) {


    val curSongDuration by viewModel.curSongDuration.collectAsState()
    val curPlaybackState by viewModel.playbackState().collectAsState()
    val songsState by viewModel.songs
    val currentSongIndex by viewModel.curSongIndex.collectAsState()
    val currentMediaMetadata by viewModel.playingMediaMetadata().collectAsState()
    val recentSong by viewModel.recentSong.collectAsState()
    val curPlaybackPosition by viewModel.currentPlaybackPosition.collectAsState()

    var scrollBySystem by remember {
        mutableStateOf(false)
    }

    val pagerState = rememberPagerState()

    LaunchedEffect(key1 = pagerState.pageCount, key2 = currentSongIndex) {
        if (currentSongIndex > -1) {
            if (pagerState.pageCount > 0) {
                scrollBySystem = true
                pagerState.scrollToPage(currentSongIndex)
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
        ) {
            TopSection(
                modifier = Modifier.align(Alignment.TopCenter),
                mediaMetadataCompat = currentMediaMetadata
            ) {
                navController.popBackStack()
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceAround,
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(16.dp)
            ) {
                AlbumArtPager(
                    songs = songsState.songs,
                    pagerState = pagerState,
                    imageLoader = viewModel.getImageLoader()
                )
                Spacer(modifier = Modifier.height(8.dp))
                TitleArtist(
                    mediaMetadataCompat = if (currentMediaMetadata == NOTHING_PLAYING) recentSong
                    else currentMediaMetadata
                )
                SeekbarSection(
                    curPlaybackPosition = curPlaybackPosition,
                    curSongDuration = curSongDuration,
                    seekToPosition = { viewModel.onEvent(PlayerEvents.SeekTo(it)) }
                )
                MediaControllerSection(
                    playbackStateCompat = curPlaybackState,
                    onPlayClicked = { viewModel.onEvent(PlayerEvents.PlayOrPause(true)) },
                    onPauseClicked = { viewModel.onEvent(PlayerEvents.PlayOrPause(false)) },
                    onSkipToPrevious = { viewModel.onEvent(PlayerEvents.SkipToPrevious) },
                    onSkipToNext = { viewModel.onEvent(PlayerEvents.SkipToNext) },
                    onForward = { viewModel.onEvent(PlayerEvents.FastForward) },
                    onRewind = { viewModel.onEvent(PlayerEvents.FastRewind) }
                )
            }
            //on pager state navigate to
            LaunchedEffect(key1 = pagerState.currentPage) {
                if (pagerState.pageCount > 0) {
                    snapshotFlow { pagerState.currentPage }.collect { page ->
                        songsState.songs.let { listOfSongs ->
                            if (!scrollBySystem) {
                                viewModel.onEvent(PlayerEvents.PlayFromMediaId(listOfSongs[page].id!!))
                                viewModel.onEvent(PlayerEvents.PlayOrPause(true))
                            }
                        }
                        scrollBySystem = false
                    }
                }
            }

        }
    }
}