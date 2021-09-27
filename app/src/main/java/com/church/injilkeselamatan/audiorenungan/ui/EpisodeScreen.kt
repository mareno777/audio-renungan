package com.church.injilkeselamatan.audiorenungan.ui

import android.support.v4.media.MediaMetadataCompat
import android.util.Log
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import com.church.injilkeselamatan.audiorenungan.R
import com.church.injilkeselamatan.audiorenungan.Screen
import com.church.injilkeselamatan.audiorenungan.data.models.MediaItemData
import com.church.injilkeselamatan.audiorenungan.data.models.MusicX
import com.church.injilkeselamatan.audiorenungan.util.Constants
import com.church.injilkeselamatan.audiorenungan.viewmodels.EpisodeViewModel

@Composable
fun EpisodeScreen(
    navController: NavController,
    parentId: String?,
    album: String?,
    episodeViewModel: EpisodeViewModel = hiltViewModel()
) {
    val songs by episodeViewModel.mediaItems.observeAsState(mutableListOf())
    var isExpanded by remember {
        mutableStateOf(false)
    }
    //songs.filter { it.mediaId == album }
    //Log.e("EpisodeScreen", "mediaId: ${songs[0].mediaId} title: ${songs[0].title}")
    Log.e("EpisodeScreen", songs.toString())

    LaunchedEffect(parentId) {
        parentId?.let {
            episodeViewModel.subscribe(it)
        }
    }

    val description = when (album) {
        "Pohon Kehidupan" -> Constants.DESC_PK
        "Belajar Takut Akan Tuhan" -> Constants.DESC_BTAT
        "Saat Teduh Bersama Tuhan" -> Constants.DESC_STBT
        else -> ""
    }
    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            item {
                Text(
                    text = album ?: "",
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
            items(songs) { song ->
                SongItem(song = song, episodeViewModel)
                Divider()
            }
        }
    }
}

@ExperimentalCoilApi
@Composable
fun SongItem(song: MediaItemData, episodeViewModel: EpisodeViewModel) {

    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(end = 8.dp)
            .clickable { episodeViewModel.playMediaId(song.mediaId) }
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = rememberImagePainter(
                    data = song.albumArtUri
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
                    text = song.subtitle,
                    style = MaterialTheme.typography.subtitle1,
                    fontSize = 14.sp,
                    color = MaterialTheme.colors.onSurface
                )

            }
        }

        Icon(
            painterResource(
                id = R.drawable.download),
            contentDescription = "Download this media",
            tint = MaterialTheme.colors.onSurface,
            modifier = Modifier
                .size(24.dp)
                .clickable {
                    // TODO: click to download media
                    episodeViewModel.downloadSong(song.mediaId)
                }
        )
    }
}