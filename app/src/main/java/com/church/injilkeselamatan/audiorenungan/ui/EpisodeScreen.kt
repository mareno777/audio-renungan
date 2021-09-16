package com.church.injilkeselamatan.audiorenungan.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import com.church.injilkeselamatan.audiorenungan.R
import com.church.injilkeselamatan.audiorenungan.Screen
import com.church.injilkeselamatan.audiorenungan.data.models.MusicX
import com.church.injilkeselamatan.audiorenungan.exoplayer.media.extensions.album
import com.church.injilkeselamatan.audiorenungan.exoplayer.media.extensions.artist
import com.church.injilkeselamatan.audiorenungan.exoplayer.media.extensions.isPlaying
import com.church.injilkeselamatan.audiorenungan.exoplayer.media.extensions.title
import com.church.injilkeselamatan.audiorenungan.viewmodels.EpisodeViewModel

@ExperimentalCoilApi
@Composable
fun EpisodeScreen(
    navController: NavController,
    album: String?,
    episodeViewModel: EpisodeViewModel = hiltViewModel()
) {

    Box(modifier = Modifier.fillMaxSize()) {
        SongList(
            viewModel = episodeViewModel,
            albumType = album,
            modifier = Modifier.align(Alignment.TopCenter)
        ) { song ->
            episodeViewModel.playMediaId(song.id)
        }
        PlayingNowSectionEpisode(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .clickable {
                    // TODO: Open music player
                    navController.navigate(Screen.PlayerScreen.route)
                }, episodeViewModel
        )
    }
}

@Composable
private fun TopSection(modifier: Modifier = Modifier) {
    val scrollState = rememberScrollState()
    var isExpanded by remember {
        mutableStateOf(false)
    }
    val description = buildAnnotatedString {
        if (isExpanded) {
            append(
                """
                Kalau kita mencari Tuhan setiap hari, 
                mengisi hati dan pikiran kita dengan Kebenaran Firman Tuhan, 
                maka kita akan lebih mudah mengenal Kehendak Tuhan.

                Kita akan lebih bisa mengalami hatiNya, 
                mengalami KehendakNya, mengalami didikanNya dan 
                mengalami Anugerah KasihNya.

                Apalagi kalau kita memperkatakan dan melakukan Firman Tuhan setiap hari, maka roh kita penuh dengan pengertian akan Firman Tuhan.  
                Dan jiwa kita akan sehat, karena mental – pikiran – perasaan dan kehendak kita penuh dengan Kebijakan Tuhan.

                Mari kita merenungkan dan melakukan Firman Tuhan setiap hari, 
                supaya hidup kita sejalan dengan Kehendak Tuhan.
                Dan hidup kita memuliakan Nama Tuhan.

                Yosea Christiono
                Gembala Jemaat JKI Injil Keselamatan Semarang

            """.trimIndent()
            )
        } else {
            append(
                """Kalau kita mencari Tuhan setiap hari, mengisi hati dan pikiran kita dengan Keben... 
                """.trimIndent()
            )
        }
    }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .padding(start = 16.dp, end = 8.dp)
            .scrollable(
                state = scrollState,
                orientation = Orientation.Vertical,
                enabled = true
            )
    ) {
        Text(
            text = "Pohon Kehidupan",
            fontFamily = sourceSansPro,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.h3,
            color = MaterialTheme.colors.onSurface
        )

        Text(
            text = description,
            fontFamily = sourceSansPro,
            style = MaterialTheme.typography.body1,
            color = MaterialTheme.colors.onSurface
        )
        Text(
            text = if (isExpanded) "Show less" else "Show more",
            color = MaterialTheme.colors.primary,
            modifier = Modifier.clickable {
                isExpanded = !isExpanded
            }
        )

    }
}

@ExperimentalCoilApi
@Composable
fun PlayingNowSectionEpisode(
    modifier: Modifier = Modifier,
    viewModel: EpisodeViewModel = hiltViewModel()
) {

    val playbackStateCompat by viewModel.playbackStateCompat.observeAsState()
    val mediaMetadataCompat by viewModel.mediaMetadataCompat.observeAsState()

    val painter = rememberImagePainter(
        data = when (mediaMetadataCompat?.album) {
            "Pohon Kehidupan" -> R.drawable.pohon_kehidupan
            "Belajar Takut Akan Tuhan" -> R.drawable.btat
            "Saat Teduh Bersama Tuhan" -> R.drawable.stbt
            else -> null
        }
    )

    Surface(
        elevation = 8.dp,
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight(0.08f)
    ) {
        Row(horizontalArrangement = Arrangement.SpaceBetween) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painter,
                    modifier = Modifier
                        .width(70.dp)
                        .fillMaxHeight(),
                    contentDescription = null,
                    contentScale = ContentScale.FillBounds
                )
                Spacer(modifier = Modifier.width(4.dp))
                Column {
                    Text(
                        text = mediaMetadataCompat?.title ?: "Unknown",
                        style = MaterialTheme.typography.caption,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 18.sp
                    )
                    Text(
                        text = mediaMetadataCompat?.artist ?: "Unknown",
                        style = MaterialTheme.typography.caption,
                        fontSize = 14.sp
                    )

                }
            }
            if (playbackStateCompat?.isPlaying == true) {
                Icon(
                    painter = rememberImagePainter(data = R.drawable.ic_pause),
                    contentDescription = null,
                    tint = MaterialTheme.colors.onSurface,
                    modifier = Modifier
                        .width(50.dp)
                        .fillMaxHeight()
                        .padding(end = 4.dp)
                        .clickable {
                            viewModel.transportControls.pause()
                        }
                )
            } else {
                Icon(
                    painterResource(id = R.drawable.ic_play_arrow),
                    contentDescription = null,
                    modifier = Modifier
                        .width(50.dp)
                        .fillMaxHeight()
                        .clickable {
                            //TODO: play or pause the music
                            viewModel.transportControls.play()

                        }
                )
            }
        }
    }
}

@ExperimentalCoilApi
@Composable
fun SongList(
    viewModel: EpisodeViewModel,
    albumType: String?,
    modifier: Modifier = Modifier,
    onItemClicked: (MusicX) -> Unit
) {
    var songList by remember {
        viewModel.songList
    }
    albumType?.let { type ->
        songList = songList.filter { it.album == type }
    }

    LazyColumn(modifier = modifier) {
        items(songList.size) {
            SongItem(songList[it], onItemClicked)
            Divider(color = Color.LightGray, thickness = 1.dp)
        }
    }
}

@ExperimentalCoilApi
@Composable
fun SongItem(song: MusicX, onItemClicked: (MusicX) -> Unit) {

    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onItemClicked(song) }) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = rememberImagePainter(
                    data = song.image
                ), modifier = Modifier.size(60.dp),
                contentDescription = null,
                contentScale = ContentScale.FillBounds
            )
            Spacer(modifier = Modifier.width(4.dp))
            Column {
                Text(
                    text = song.title,
                    style = MaterialTheme.typography.caption,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp,
                    color = MaterialTheme.colors.onSurface
                )
                Text(
                    text = song.artist,
                    style = MaterialTheme.typography.subtitle1,
                    fontSize = 14.sp,
                    color = MaterialTheme.colors.onSurface
                )

            }
        }
    }
}