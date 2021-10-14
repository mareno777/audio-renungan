package com.church.injilkeselamatan.audiorenungan.feature_music.presentation.player

import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.widget.SeekBar
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.KeyboardArrowLeft
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.church.injilkeselamatan.audiorenungan.R
import com.church.injilkeselamatan.audiorenungan.feature_music.presentation.player.components.HorizontalPagerWithOffsetTransition
import com.church.injilkeselamatan.audiorenungan.feature_music.exoplayer.media.extensions.displaySubtitle
import com.church.injilkeselamatan.audiorenungan.feature_music.exoplayer.media.extensions.isPlaying
import com.church.injilkeselamatan.audiorenungan.feature_music.exoplayer.media.extensions.title
import com.church.injilkeselamatan.audiorenungan.feature_music.ui.sourceSansPro
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.flow.collect
import java.text.SimpleDateFormat
import java.util.*

@ExperimentalPagerApi
@Composable
fun PlayerScreen(navController: NavController, viewModel: PlayerViewModel = hiltViewModel()) {

    val mediaMetadataCompat by viewModel.mediaMetadataCompat.observeAsState()
    val playbackStateCompat by viewModel.playbackStateCompat.observeAsState()
    val curSongDuration by viewModel.curSongDuration.observeAsState()
    val curPlayingPosition by viewModel.curPlayerPosition.observeAsState()
    val currentSongIndex by viewModel.curSongIndex.observeAsState()
    val songs by viewModel.songs.observeAsState()

    var scrollBySystem by remember {
        mutableStateOf(false)
    }


    val pagerState = rememberPagerState(
        pageCount = songs?.size ?: 0,
        initialOffscreenLimit = 2
    )

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        LaunchedEffect(currentSongIndex) {
            currentSongIndex?.let { index ->
                if (index > 0) {
                    scrollBySystem = true
                    pagerState.animateScrollToPage(index)
                    scrollBySystem = false
                }
            }
        }

        TopSection(
            navController = navController,
            modifier = Modifier.align(Alignment.TopCenter)
        )
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceAround,
            modifier = Modifier.align(Alignment.Center)
        ) {
            ImageTitleArtist(
                mediaMetadataCompat = mediaMetadataCompat,
                playbackStateCompat = playbackStateCompat,
                pagerState = pagerState,
                songs = songs
            )
            SeekbarSection(
                curPlayerPosition = curPlayingPosition,
                curSongDuration = curSongDuration,
                seekToPosition = { viewModel.seekTo(it) }
            )
            MediaControllerSection(
                playbackStateCompat = playbackStateCompat,
                viewModel = viewModel
            )
        }
        //on pager state navigate to
        LaunchedEffect(key1 = pagerState) {
            snapshotFlow { pagerState.currentPage }.collect { page ->
                songs?.let { mediaMetadataCompats ->
                    if (!scrollBySystem)
                        viewModel.playMediaId(mediaMetadataCompats[page].description.mediaId!!)
                    viewModel.play()
                }
            }
        }
    }

}

@Composable
private fun TopSection(modifier: Modifier = Modifier, navController: NavController) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Icon(
            Icons.Rounded.KeyboardArrowLeft,
            contentDescription = null,
            modifier = Modifier
                .size(30.dp)
                .clickable {
                    navController.popBackStack()
                },
            tint = MaterialTheme.colors.onSurface
        )

        Text(
            text = "Now Playing",
            fontFamily = sourceSansPro,
            fontSize = 20.sp,
            color = MaterialTheme.colors.onSurface,
            fontWeight = FontWeight.SemiBold
        )
        Icon(
            Icons.Rounded.Info,
            contentDescription = null,
            tint = MaterialTheme.colors.onSurface,
            modifier = Modifier.size(26.dp)
        )
    }
}


@ExperimentalPagerApi
@Composable
private fun ImageTitleArtist(
    mediaMetadataCompat: MediaMetadataCompat?,
    playbackStateCompat: PlaybackStateCompat?,
    modifier: Modifier = Modifier,
    songs: List<MediaMetadataCompat>?,
    pagerState: PagerState
) {
    Column(
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.padding(16.dp)
    ) {
        HorizontalPagerWithOffsetTransition(pagerState, songs)
        Spacer(modifier = Modifier.padding(vertical = 16.dp))
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = mediaMetadataCompat?.title ?: "Unknown",
                color = MaterialTheme.colors.onSurface
            )
            Text(
                text = mediaMetadataCompat?.displaySubtitle ?: "Unknown",
                color = MaterialTheme.colors.onSurface
            )
        }
    }
}

@ExperimentalPagerApi
@Composable
fun SeekbarSection(
    curPlayerPosition: Long?,
    curSongDuration: Long?,
    seekToPosition: (Long) -> Unit
) {
    val dateFormat = SimpleDateFormat("mm:ss", Locale.getDefault())

    val context = LocalContext.current

    var shouldUpdateSeekbar by remember {
        mutableStateOf(true)
    }
    var currentPositionText by remember {
        mutableStateOf("00:00")
    }
    val seekBarView = remember {
        SeekBar(context)
    }

    val seekBarListener = remember {
        object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(
                seekBar: SeekBar?,
                progress: Int,
                fromUser: Boolean
            ) {
                currentPositionText = dateFormat.format(progress.toLong())
            }

            override fun onStartTrackingTouch(seekbar: SeekBar?) {
                shouldUpdateSeekbar = false
            }

            override fun onStopTrackingTouch(seekbar: SeekBar?) {
                seekbar?.let {
                    seekToPosition(it.progress.toLong())
                    shouldUpdateSeekbar = true
                }
            }
        }
    }

    DisposableEffect(key1 = seekBarView) {
        seekBarView.setOnSeekBarChangeListener(seekBarListener)
        onDispose {
            seekBarView.setOnSeekBarChangeListener(null)
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = currentPositionText,
            color = MaterialTheme.colors.onSurface
        )
        AndroidView(
            modifier = Modifier.weight(2f),
            factory = {
                seekBarView
            }
        ) { seekBarView ->

            seekBarView.max = curSongDuration?.toInt() ?: 0

            if (shouldUpdateSeekbar) {
                seekBarView.progress = curPlayerPosition?.toInt() ?: 0
            }
        }
        Text(
            text = dateFormat.format(curSongDuration ?: 0L),
            color = MaterialTheme.colors.onSurface
        )
    }
}

@Composable
fun MediaControllerSection(
    modifier: Modifier = Modifier,
    playbackStateCompat: PlaybackStateCompat?,
    viewModel: PlayerViewModel,
    iconSize: Dp = 48.dp,
    iconColor: Color = MaterialTheme.colors.onSurface
) {
    Row(
        horizontalArrangement = Arrangement.SpaceAround,
        modifier = modifier
    ) {

        Icon(
            painterResource(id = R.drawable.ic_skip_previous),
            contentDescription = null,
            modifier = Modifier
                .size(iconSize)
                .clickable {
                    viewModel.skipToPrevious()
                },
            tint = iconColor
        )

        Spacer(modifier = Modifier.padding(horizontal = 8.dp))

        if (playbackStateCompat?.isPlaying == true) {
            Icon(
                painterResource(id = R.drawable.ic_pause),
                contentDescription = null,
                tint = MaterialTheme.colors.onSurface,
                modifier = Modifier
                    .size(iconSize)
                    .clickable {
                        viewModel.pause()
                    }
            )
        } else {
            Icon(
                painterResource(id = R.drawable.ic_play_arrow),
                contentDescription = null,
                tint = MaterialTheme.colors.onSurface,
                modifier = Modifier
                    .size(iconSize)
                    .clickable {
                        viewModel.play()
                    }
            )
        }

        Spacer(modifier = Modifier.padding(horizontal = 8.dp))

        Icon(
            painterResource(id = R.drawable.ic_skip_next),
            contentDescription = null,
            modifier = Modifier
                .size(iconSize)
                .clickable {
                    viewModel.skipToNext()
                },
            tint = iconColor
        )

    }
}