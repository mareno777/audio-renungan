package com.church.injilkeselamatan.audiorenungan.feature_music.presentation.player

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.church.injilkeselamatan.audiorenungan.feature_music.presentation.player.components.ImageTitleArtist
import com.church.injilkeselamatan.audiorenungan.feature_music.presentation.player.components.MediaControllerSection
import com.church.injilkeselamatan.audiorenungan.feature_music.presentation.player.components.SeekbarSection
import com.church.injilkeselamatan.audiorenungan.feature_music.presentation.player.components.TopSection
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.flow.collect

@ExperimentalPagerApi
@Composable
fun PlayerScreen(navController: NavController, viewModel: PlayerViewModel = hiltViewModel()) {

    val mediaMetadataCompat by viewModel.mediaMetadataCompat.collectAsState()
    val playbackStateCompat by viewModel.playbackStateCompat.collectAsState()
    val curSongDuration by viewModel.curSongDuration.collectAsState()
    val curPlayingPosition by viewModel.currentPlayingPosition().collectAsState(0L)
    val currentSongIndex by viewModel.curSongIndex.collectAsState()
    val songsState by viewModel.songs

    var scrollBySystem by remember {
        mutableStateOf(false)
    }


    val pagerState = rememberPagerState(
        pageCount = songsState.songs.size,
        initialOffscreenLimit = 2
    )

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        LaunchedEffect(key1 = currentSongIndex, key2 = pagerState.pageCount) {
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
            ImageTitleArtist(
                mediaMetadataCompat = mediaMetadataCompat,
                playbackStateCompat = playbackStateCompat,
                pagerState = pagerState,
                songs = songsState.songs
            )
            SeekbarSection(
                curPlayingPosition = curPlayingPosition,
                curSongDuration = curSongDuration,
                seekToPosition = { viewModel.seekTo(it) }
            )
            MediaControllerSection(
                playbackStateCompat = playbackStateCompat,
                onPlayClicked = { viewModel.play() },
                onPauseClicked = { viewModel.pause() },
                onSkipToPrevious = { viewModel.skipToPrevious() },
                onSkipToNext = { viewModel.skipToNext() },
            )
        }
        //on pager state navigate to
        LaunchedEffect(key1 = pagerState.currentPage) {
            if (pagerState.pageCount > 0) {
                snapshotFlow { pagerState.currentPage }.collect { page ->
                    songsState.songs.let { listOfSongs ->
                        if (!scrollBySystem) {
                            viewModel.playMediaId(listOfSongs[page].id)
                            viewModel.play()
                        }
                    }
                    scrollBySystem = false
                }
            }
        }

    }
}