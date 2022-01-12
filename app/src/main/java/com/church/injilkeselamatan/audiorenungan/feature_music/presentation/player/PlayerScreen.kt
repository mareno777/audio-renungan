package com.church.injilkeselamatan.audiorenungan.feature_music.presentation.player

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.church.injilkeselamatan.audiorenungan.feature_music.presentation.player.components.*
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.rememberPagerState

@ExperimentalPagerApi
@Composable
fun PlayerScreen(navController: NavController, viewModel: PlayerViewModel = hiltViewModel()) {

    val mediaMetadataCompat by viewModel.nowPlaying.collectAsState()
    val playbackStateCompat by viewModel.playbackState.collectAsState()
    val curSongDuration by viewModel.curSongDuration.collectAsState()
    val curPlayingPosition by viewModel.updateCurrentPlayingPosition().collectAsState(0L)
    val currentSongIndex by viewModel.curSongIndex.collectAsState()
    val songsState by viewModel.songs

    var scrollBySystem by remember {
        mutableStateOf(false)
    }

    val pagerState = rememberPagerState()

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            LaunchedEffect(key1 = true, key2 = pagerState.pageCount) {
                Log.d(
                    "PlayerScreen",
                    "songIndex: $currentSongIndex, pageCount: ${pagerState.pageCount}"
                )
                if (currentSongIndex > -1) {
                    if (pagerState.pageCount > 0) {
                        scrollBySystem = true
                        pagerState.scrollToPage(currentSongIndex)
                    }
                }
            }

            LaunchedEffect(key1 = currentSongIndex) {
                Log.d(
                    "PlayerScreen",
                    "songIndex: $currentSongIndex, pageCount: ${pagerState.pageCount}"
                )
                if (currentSongIndex > -1) {
                    if (pagerState.pageCount > 0) {
                        scrollBySystem = true
                        pagerState.animateScrollToPage(currentSongIndex)
                    }
                }
            }

            TopSection(
                modifier = Modifier.align(Alignment.TopCenter)
            ) {
                navController.popBackStack()
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceAround,
                modifier = Modifier.align(Alignment.Center)
            ) {
                AlbumArtPager(
                    songs = songsState.songs,
                    pagerState = pagerState
                )
                Spacer(modifier = Modifier.height(8.dp))
                ImageTitleArtist(
                    mediaMetadataCompat = mediaMetadataCompat,
                    playbackStateCompat = playbackStateCompat
                )
                SeekbarSection(
                    curPlayingPosition = curPlayingPosition,
                    curSongDuration = curSongDuration,
                    seekToPosition = { viewModel.onEvent(PlayerEvents.SeekTo(it)) }
                )
                MediaControllerSection(
                    playbackStateCompat = playbackStateCompat,
                    onPlayClicked = { viewModel.onEvent(PlayerEvents.PlayOrPause(true)) },
                    onPauseClicked = { viewModel.onEvent(PlayerEvents.PlayOrPause(false)) },
                    onSkipToPrevious = { viewModel.onEvent(PlayerEvents.SkipToPrevious) },
                    onSkipToNext = { viewModel.onEvent(PlayerEvents.SkipToNext) },
                )
            }
            //on pager state navigate to
            LaunchedEffect(key1 = pagerState.currentPage) {
                if (pagerState.pageCount > 0) {
                    snapshotFlow { pagerState.currentPage }.collect { page ->
                        songsState.songs.let { listOfSongs ->
                            if (!scrollBySystem) {
                                Log.d("PlayerScreen", "onExecuted")
                                viewModel.onEvent(PlayerEvents.PlayFromMediaId(listOfSongs[page].id))
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